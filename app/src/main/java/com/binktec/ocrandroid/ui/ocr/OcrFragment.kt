package com.binktec.ocrandroid.ui.ocr

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binktec.ocrandroid.GlideApp

import com.binktec.ocrandroid.R
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.di.Injectable
import kotlinx.android.synthetic.main.ocr_fragment.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import java.io.IOException
import java.text.SimpleDateFormat
import android.support.v4.content.FileProvider
import android.widget.Toast
import com.binktec.ocrandroid.data.model.OcrResponse
import com.binktec.ocrandroid.data.model.Resource
import com.binktec.ocrandroid.data.model.Status
import com.binktec.ocrandroid.ui.main.MainViewModel
import com.binktec.ocrandroid.utils.FilePickUtils
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
        cameraBtn.setOnClickListener{
            currentTask = 0
            if (checkAndReqPermission()) dispatchTakePictureIntent()
        }
        galleryBtn.setOnClickListener{
            currentTask = 1
            if (checkAndReqPermission()) galleryIntent()
        }
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

    private fun galleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, galleryCode)
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
        }
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
        ocrViewModel.newReq(ocrRequest)
        showResultView()
    }

    private fun updateUi(req: OcrRequest) {
        context?.let { GlideApp.with(it).load(File(req.imagePath)).into(req_image) }
    }

    private fun updateUi(response: Resource<OcrResponse>) {
        if (response.status == Status.LOADING) res_progress.visibility = View.VISIBLE
        else {
            res_progress.visibility = View.GONE
            var txt = ""
            if (response.status == Status.SUCCESS) {
                txt = response.data?.resultTxt.orEmpty()
            }
            else if (response.status == Status.ERROR) txt = "Error occured" + response.message
            response_txt.text = txt
        }
    }

    private fun showResultView() {
        cameraBtn.visibility = View.GONE
        galleryBtn.visibility = View.GONE
        pick_image_text.visibility = View.GONE
        res_card.visibility = View.VISIBLE
        req_image.visibility = View.VISIBLE
    }


}
