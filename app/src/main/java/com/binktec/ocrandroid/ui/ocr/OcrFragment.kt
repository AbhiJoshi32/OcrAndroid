package com.binktec.ocrandroid.ui.ocr

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binktec.ocrandroid.GlideApp

import com.binktec.ocrandroid.R
import com.binktec.ocrandroid.di.Injectable
import kotlinx.android.synthetic.main.ocr_fragment.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import java.io.IOException
import java.text.SimpleDateFormat
import android.support.v4.content.FileProvider
import android.widget.Toast
import com.binktec.ocrandroid.data.model.*
import com.binktec.ocrandroid.ui.main.MainViewModel
import com.binktec.ocrandroid.utils.FilePickUtils
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class OcrFragment : Fragment(), Injectable {

    companion object {
        private const val OCR_REQ_NAME = "name"
        private const val OCR_REQ_PATH = "path"
        fun newInstance(name: String?, path: String?): OcrFragment {
            val ocrFragment = OcrFragment()
            val args = Bundle()
            args.putString(OCR_REQ_NAME,name)
            args.putString(OCR_REQ_PATH, path)
            ocrFragment.arguments = args
            return ocrFragment
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var ocrViewModel: OcrViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var imagePath: String
    private var currentTask = 0
    private val takePhotoCode = 1
    private val galleryCode = 2
    private val permissionId = 3
    private val contactPermissionId = 4
    private lateinit var  imageName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ocr_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ocrViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(OcrViewModel::class.java)
        if (activity != null) {
            mainViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)
            mainViewModel.title.value = "Ocr Request"
        }
        val name = arguments?.getString(OCR_REQ_NAME)
        val path = arguments?.getString(OCR_REQ_PATH)
        ocrViewModel.ocrRequest.observe(this, Observer {
            Timber.d("Observer new req$it")
            it?.let { updateUi(it) }
        })
        ocrViewModel.response.observe(this, Observer {
            Timber.d("Observed response $it")
            it?.let {updateUi(it) }
        })
        if (!path.isNullOrBlank() && !name.isNullOrBlank()) {
            ocrViewModel.setReqParam(name, path)
        }
        ocrViewModel.reqParam.observe(this, Observer {
            if (it != null) showResultView()
        })
        ocrViewModel.entities.observe(this, Observer {
            it?.let { updateTextUi(it) }
        })
        cameraBtn.setOnClickListener{
            currentTask = 0
            if (checkAndReqPermission()) dispatchTakePictureIntent()
        }
        galleryBtn.setOnClickListener{
            currentTask = 1
            if (checkAndReqPermission()) galleryIntent()
        }
        create_contact_btn.setOnClickListener {
            if (checkContactPermission()) {
                contactIntent()
            }
        }
    }

    private fun galleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, galleryCode)
    }


    private fun contactIntent() {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION)
        Timber.d("${emailEdit.text} ${numberEdit.text} ${nameEdit.text} ${companyEdit.text}")
        intent.type = ContactsContract.RawContacts.CONTENT_TYPE
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, emailEdit.text)
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.PHONE, numberEdit.text)
                .putExtra(ContactsContract.Intents.Insert.POSTAL, placeEdit.text)
                .putExtra(ContactsContract.Intents.Insert.NAME, nameEdit.text.toString())
                .putExtra(ContactsContract.Intents.Insert.COMPANY, companyEdit.text)
        startActivity(intent)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imageName = "JPEG_" + timeStamp + "_"
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        imagePath = image.absolutePath
        return image
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity?.packageManager) != null) {
            try {
                val photoFile = createImageFile()
                val photoURI = FileProvider.getUriForFile(context!!,
                        "com.binktec.android.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, takePhotoCode)
            } catch (ex:IOException) {
                Timber.e(ex)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                takePhotoCode -> {
                    sendReq()
                }
                galleryCode -> {
                    if (data != null && context != null) {
                        val imageUri = data.data
                        val gpath = FilePickUtils.getPath(context!!,imageUri)
                        if (gpath != null) {
                            imagePath = gpath
                            imageName = File(imagePath).name
                            sendReq()
                        }
                    }
                }
            }
        }
    }

    private fun sendReq() {
        val ocrRequest = OcrRequest(imagePath = imagePath, time = System.currentTimeMillis() / 1000, name = imageName)
        saveBitmapToFile(File(imagePath))
        ocrViewModel.newReq(ocrRequest)
        showResultView()
    }

    private fun updateTextUi(it: Resource<TextEntities>) {
        when {
            it.status == Status.LOADING -> {
                res_progress.visibility = View.VISIBLE
                processText.text = getString(R.string.proces_contact_text)
                processText.visibility = View.VISIBLE
            }
            it.status == Status.SUCCESS -> {
                contactCard.visibility = View.VISIBLE
                res_progress.visibility = View.GONE
                processText.visibility = View.INVISIBLE
                emailEdit.setText(it.data?.email)
                nameEdit.setText(it.data?.contactName)
                numberEdit.setText(it.data?.number)
                placeEdit.setText(it.data?.place)
                companyEdit.setText(it.data?.company)
            }
            else -> {
                Toast.makeText(context,"Cant find any contact info",Toast.LENGTH_LONG).show()
                contactCard.visibility = View.GONE
                res_progress.visibility = View.GONE
                processText.visibility = View.INVISIBLE
            }
        }
    }


    private fun updateUi(req: OcrRequest) {
        context?.let { GlideApp.with(it).load(File(req.imagePath)).into(req_image) }
    }

    private fun updateUi(response: Resource<OcrResponse>) {
        var txt = ""
        when {
            response.status == Status.LOADING -> {
                res_progress.visibility = View.VISIBLE
                processText.text = getString(R.string.ocr_process_text)
                processText.visibility = View.VISIBLE
            }
            response.status == Status.SUCCESS -> {
                res_progress.visibility = View.GONE
                processText.visibility = View.INVISIBLE
                res_card.visibility = View.VISIBLE

                txt = response.data?.resultTxt.orEmpty()
                if (txt.isNotEmpty()) {
                    res_progress.visibility = View.GONE
                    processText.text = getString(R.string.cant_process_ocr)
                    ocrViewModel.textToExtract.value = txt
                }
            }
            else -> {
                txt = "Error occurred" + response.message
            }
        }
        response_txt.text = txt
    }

    private fun showResultView() {
        cameraBtn.visibility = View.GONE
        galleryBtn.visibility = View.GONE
        pick_image_text.visibility = View.GONE
//        res_card.visibility = View.VISIBLE
        req_image.visibility = View.VISIBLE
    }

    private fun saveBitmapToFile(file:File):File{
        try {
            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image

            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val reqSize = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while(o.outWidth / scale / 2 >= reqSize &&
                            o.outHeight / scale / 2 >= reqSize) {
                scale *= 2
            }

            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream)

            return file
        } catch (e:Exception) {
            Timber.e(e)
            return file
        }
    }
    private fun checkContactPermission(): Boolean {
        val contactPermission = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_CONTACTS)
        if (contactPermission != PackageManager.PERMISSION_GRANTED) {
            val ar = arrayOf(Manifest.permission.WRITE_CONTACTS)
            requestPermissions(ar,contactPermissionId)
            return false
        }
        return true
    }

    private fun checkAndReqPermission(): Boolean {
        val camPermission = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        val writePermission = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(activity!!,Manifest.permission.READ_EXTERNAL_STORAGE)
        val listPermissionNeeded = ArrayList<String>()
        if (camPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.CAMERA)
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!listPermissionNeeded.isEmpty()) {
            requestPermissions(listPermissionNeeded.toTypedArray(),permissionId)
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Timber.d("Req per result$requestCode")
        when(requestCode) {
            permissionId -> {
                val perms = HashMap<String,Int>()
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        if (currentTask == 0) dispatchTakePictureIntent()
                        else galleryIntent()
                    } else {
                        Toast.makeText(context,"Allow permission to use ocr",Toast.LENGTH_LONG).show()
                    }
                }
            }
            contactPermissionId -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) contactIntent()
            }
        }
    }
}
