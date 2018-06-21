package com.binktec.ocrandroid.ui.ocr

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binktec.ocrandroid.GlideApp

import com.binktec.ocrandroid.R
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.data.model.response.OcrResponse
import com.binktec.ocrandroid.di.Injectable
import kotlinx.android.synthetic.main.ocr_fragment.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import java.io.IOException
import java.text.SimpleDateFormat
import android.support.v4.content.FileProvider
import com.binktec.ocrandroid.data.model.Resource
import com.binktec.ocrandroid.utils.FilePickUtils
import java.util.*


class OcrFragment : Fragment(), Injectable {

    companion object {
        private const val OCR_REQ_NAME = "name"
        private const val OCR_REQ_PATH = "path"
        fun newInstance(name: String, path: String?): OcrFragment {
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
    private lateinit var imagePath: String
    private val takePhotoCode = 1
    private val galleryCode = 2
    private lateinit var  imageName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ocr_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ocrViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(OcrViewModel::class.java)
        val name = arguments?.getString(OCR_REQ_NAME)
        val path = arguments?.getString(OCR_REQ_PATH)
        ocrViewModel.ocrRequest.observe(this, Observer {
            it?.let { updateUi(it) }
        })
        ocrViewModel.response.observe(this, Observer {
            it?.let {updateUi(it) }
        })
        ocrViewModel.setReqParam(name, path)
        cameraBtn.setOnClickListener{
            dispatchTakePictureIntent()
        }
        galleryBtn.setOnClickListener{
            galleryIntent()
        }
    }

    private fun galleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, galleryCode)
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
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (resultCode) {
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
    }

    private fun updateUi(req: OcrRequest) {
        context?.let { GlideApp.with(it).load(Uri.parse(req.imagePath)).into(req_image) }
    }

    private fun updateUi(response: Resource<OcrResponse>) {
        var txt = ""
        for (res in response.data!!.parsedResults) {
            txt += res.parsedText
        }
        response_txt.text = txt
    }

}
