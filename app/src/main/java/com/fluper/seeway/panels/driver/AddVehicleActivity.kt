package com.fluper.seeway.panels.driver

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fluper.seeway.R
import com.fluper.seeway.base.BaseActivity
import com.fluper.seeway.onBoard.adapter.DriverVehicleInfoAdapter
import com.fluper.seeway.onBoard.adapter.RemovePictures
import com.fluper.seeway.onBoard.adapter.UploadImagesAdapter
import com.fluper.seeway.onBoard.model.ImageUploadModel
import com.fluper.seeway.onBoard.model.VehicleInfoModel
import com.fluper.seeway.utilitarianFiles.statusBarFullScreenWithBackground
import kotlinx.android.synthetic.main.fragment_add_vehicle.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddVehicleActivity : BaseActivity(), View.OnClickListener {

    private var IMAGE_PICK_CODE = 100
    private var IMAGE_CAPTURE_CODE = 200
    private val PERMISSION_CODE = 300
    private val PERMISSION_CODE1 = 400
    var image_uri: Uri? = null
    private var imageUri1: Uri? = null
    val uvi_arrayList = ArrayList<ImageUploadModel>()
    val ucd_arrayList = ArrayList<ImageUploadModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_vehicle)
        statusBarFullScreenWithBackground()
        initClickListener()
        imgUploadRec()
    }

    private fun initClickListener() {
        img_back_addv.setOnClickListener(this)
        vehicle_img_upload.setOnClickListener(this)
        car_doc_img_upload.setOnClickListener(this)
        ll_vehicle_registration.setOnClickListener(this)
        btn_save_addv.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back_addv->{this.onBackPressed()}
            R.id.vehicle_img_upload -> {
                uploadImg()
                IMAGE_PICK_CODE = 100
                IMAGE_CAPTURE_CODE = 200
                dummy_vehicle_img.visibility = View.GONE
                vehicle_img_rec.visibility = View.VISIBLE
            }

            R.id.car_doc_img_upload -> {
                uploadImg()
                IMAGE_PICK_CODE = 101
                IMAGE_CAPTURE_CODE = 201
                dummy_car_img.visibility = View.GONE
                car_doc_rec.visibility = View.VISIBLE
            }
            R.id.ll_vehicle_registration->{
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    this,
                    R.style.DatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val newDate = Calendar.getInstance()
                        newDate[year, monthOfYear] = dayOfMonth
                        val simpleDateFormat =
                            SimpleDateFormat("dd-MM-yyyy")
                        val date = simpleDateFormat.format(newDate.time)
                        etCardDate_driver.visibility = View.VISIBLE
                        etCardDate_driver.setText(date)
                    },
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                datePickerDialog?.show()
            }
            R.id.btn_save_addv->{
                val vn_name : String = edt_vn_driver.text.toString().trim()
                val vmn_model_number : String = edt_vmn_driver.text.toString().trim()
                if(!vn_name.isBlank() && !vmn_model_number.isBlank())
                    initView(vn_name,vmn_model_number)
                val i  = Intent(this, ProfileCreationDriverActivity::class.java)
                i.putExtra("vn_name", vn_name)
                i.putExtra("vmn_model_number", vmn_model_number)
                startActivity(i)
            }
        }
    }

    private fun initView(vn_name : String,  vmn_model_number : String){
        val vehicleInfoList = ArrayList<VehicleInfoModel>()
        vehicleInfoList.add(VehicleInfoModel(vn_name,vmn_model_number))
        var  adapter = DriverVehicleInfoAdapter(vehicleInfoList, this)
    }
    private fun imgUploadRec(){
        vehicle_img_rec.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            true
        )
        car_doc_rec.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            true
        )

        rg_relation_vehicle.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioOwner -> edt_describ.visibility = View.GONE
                R.id.radioTenant -> edt_describ.visibility = View.GONE
                R.id.radioLease -> edt_describ.visibility = View.GONE
                R.id.radioOther -> edt_describ.visibility = View.VISIBLE
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_CODE)
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = this.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_CODE1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    openCamera()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            else->super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            if (data!!.clipData != null) {
                val count = data!!.clipData!!
                    .itemCount
                for (i in 0 until count) {
                    imageUri1 = data!!.clipData!!.getItemAt(i).uri

                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri1)

                    uvi_arrayList.add(ImageUploadModel(bitmap))
                }
                val uploadImageAdapter = UploadImagesAdapter(uvi_arrayList, this,object :
                    RemovePictures {
                    override fun removePictureId(picsCount: Int) {
                        if (picsCount==0){
                            dummy_vehicle_img.visibility = View.VISIBLE
                            vehicle_img_rec.visibility = View.GONE
                        }
                    }
                })
                vehicle_img_rec.adapter = uploadImageAdapter
            }
            else{
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)

                uvi_arrayList.add(ImageUploadModel(bitmap))
                val uploadImageAdapter = UploadImagesAdapter(uvi_arrayList, this,object :
                    RemovePictures {
                    override fun removePictureId(picsCount: Int) {
                        if (picsCount==0){
                            dummy_vehicle_img.visibility = View.VISIBLE
                            vehicle_img_rec.visibility = View.GONE
                        }
                    }
                })
                vehicle_img_rec.adapter = uploadImageAdapter
            }
        }
        if(resultCode == Activity.RESULT_OK && requestCode == 200){

            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image_uri)

            uvi_arrayList.add(ImageUploadModel(bitmap))

            val uploadImageAdapter = UploadImagesAdapter(uvi_arrayList, this,object :
                RemovePictures {
                override fun removePictureId(picsCount: Int) {
                    if (picsCount==0){
                        dummy_vehicle_img.visibility = View.VISIBLE
                        vehicle_img_rec.visibility = View.GONE
                    }
                }
            })
            vehicle_img_rec.adapter = uploadImageAdapter
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            if (data!!.clipData != null) {
                val count = data!!.clipData!!
                    .itemCount
                for (i in 0 until count) {
                    imageUri1 = data!!.clipData!!.getItemAt(i).uri

                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri1)

                    ucd_arrayList.add(ImageUploadModel(bitmap))
                }
                val uploadImageAdapter = UploadImagesAdapter(ucd_arrayList, this,object :
                    RemovePictures {
                    override fun removePictureId(picsCount: Int) {
                        if (picsCount==0){
                            dummy_car_img.visibility = View.VISIBLE
                            car_doc_rec.visibility = View.GONE
                        }
                    }
                })
                car_doc_rec.adapter = uploadImageAdapter
            }
            else{
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)

                ucd_arrayList.add(ImageUploadModel(bitmap))
                val uploadImageAdapter = UploadImagesAdapter(ucd_arrayList, this,object :
                    RemovePictures {
                    override fun removePictureId(picsCount: Int) {
                        if (picsCount==0){
                            dummy_car_img.visibility = View.VISIBLE
                            car_doc_rec.visibility = View.GONE
                        }
                    }
                })
                car_doc_rec.adapter = uploadImageAdapter
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
        if(resultCode == Activity.RESULT_OK && requestCode == 201){
            val bitmap = MediaStore.Images.Media.getBitmap(this?.contentResolver, image_uri)
            uvi_arrayList.clear()
            ucd_arrayList.add(ImageUploadModel(bitmap))


            val uploadImageAdapter = UploadImagesAdapter(ucd_arrayList, this,object :
                RemovePictures {
                override fun removePictureId(picsCount: Int) {
                    if (picsCount==0){
                        dummy_car_img.visibility = View.VISIBLE
                        car_doc_rec.visibility = View.GONE
                    }
                }
            })
            car_doc_rec.adapter = uploadImageAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun uploadImg() {
        val dialog = Dialog(this!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.open_cemera)

        val btn_cemera = dialog.findViewById<Button>(R.id.btn_cemera) as TextView

        val btn_gellery = dialog.findViewById<View>(R.id.btn_gellery) as Button
        btn_gellery.setOnClickListener {
            if (this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
                dialog.dismiss()
            } else {
                //permission already granted
                pickImageFromGallery()
                dialog.dismiss()
            }

            dialog.dismiss()
        }

        btn_cemera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (this.checkSelfPermission(android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    //permission was not enabled
                    val permission = arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                    dialog.dismiss()
                } else {
                    //permission already granted
                    openCamera()
                    dialog.dismiss()
                }
            } else {
                //system os is < marshmallow
                openCamera()
                dialog.dismiss()
            }
            dialog.dismiss()

        }
        dialog.show()
    }
}