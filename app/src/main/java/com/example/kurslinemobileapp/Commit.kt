package com.example.kurslinemobileapp

import android.net.Uri
import android.util.Base64

/*
val mainpageFragment=MainPageFragment()
val registrationpageFragment=RegisterFragment()
val favoritesFragment=FavoritePageFragment()

makeCurrentFragment(mainpageFragment)

binding.bottomNav.setOnNavigationItemSelectedListener {
    when(it.itemId){
        R.id.mainPageFragment->makeCurrentFragment(mainpageFragment)
        R.id.accountFragment->makeCurrentFragment(registrationpageFragment)
        R.id.favorites->makeCurrentFragment(favoritesFragment)
    }
    true
}

 */
/*
/*        val username = emailLoginEditText.text.toString()
        val password = passwordLoginEditText.text.toString()*/
sharedPreferences.edit().putString("user_type", response.).apply()
        sharedPreferences.edit().putString("username", username).apply()
        sharedPreferences.edit().putString("password", password).apply()
        sharedPreferences.edit().putString("company", company).apply()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()*/
/*
/*     fun selectCertificate(view: View){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

     fun downloadPhotoFromGalery() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                this.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)

                            certificateImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                selectedPicture
                            )
                            certificateImage.setImageBitmap(selectedBitmap)
                        }
                        val cervicatePath = getPathFromURI(selectedPicture)
                        companyPhoto.setText(cervicatePath!!.substring((cervicatePath.length - 8) , cervicatePath.length)+".JPG")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@RegisterCompanyActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }*/

        val language = sharedPreferences.getString("language", "DEFAULT")
        if(language.equals("av")){
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Language Selected")
            builder.setMessage("You have selected 'Azerbaijan' language.")
            builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        if (language.equals("en")){
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Language Selected")
            builder.setMessage("You have selected 'English' language.")
            builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
*/
/*
    private fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
    }
*/
/*    @SuppressLint("NewApi")
    fun getPathFromURI(uri: Uri?): String? {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor: Cursor? = this.getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf<String>(id), null
        )
        val columnIndex = cursor?.getColumnIndex(column[0])
        if (cursor!!.moveToFirst()) {
            filePath = cursor.getString(columnIndex!!)
        }
        cursor.close()
        return filePath
    }*/
/*    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {
            var path: String? = null
            val clipData = data!!.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    Log.d("URI", imageUri.path!!)
                    try {
                        val inputStream: InputStream? =
                            this.getContentResolver().openInputStream(imageUri)
                        // Static.sellectImageIsChange = true;

                        path =getPathFromURI(imageUri)

                        Log.d("TAG", "File Path: $path")
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            } else {
                val imageUri = data.data
                try {
                    val inputStream: InputStream? =
                        this.getContentResolver().openInputStream(imageUri!!)
                    path = getPathFromURI(imageUri)
                    companyPhoto.setText(path!!)
                    //                    Static.sellectImageIsChange = true;
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }*/


/*        val imageList = mutableListOf<PhotoUpload>()

        for (photoUpload in selectedPhotos) {
            val base64Image = encodeImageToBase64(photoUpload.uri)
            val imageData = PhotoUpload(photoUpload.photoName,base64Image.toUri())
            imageList.add(imageData)
            println("Base64"+base64Image)
        }*/


/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val clipData = data.clipData
                selectedPhotos.clear() // Clear the existing selection
                for (i in 0 until clipData!!.itemCount) {
//                    val name =clipData.getItemAt(i).text.toString()
                    val uri = clipData.getItemAt(i).uri
                    val photoName = "selected_photo_$i.jpg"
                    selectedPhotos.add(PhotoUpload(photoName,uri))

                    println(selectedPhotos)
                }
            } else if (data?.data != null) {
                val uri = data.data
                val imagePath = uri?.let { getRealPathFromURI(it) }
                selectedPhotos.clear() // Clear the existing selection
                selectedPhotos.add(PhotoUpload(imagePath!!,uri!!))
                println(selectedPhotos)
            }

            val adapter = viewPagerCourseUpload.adapter as? PhotoPagerAdapter
            adapter?.photoList = selectedPhotos
            adapter?.notifyDataSetChanged()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }
*/

/*
fun encodeImageToBase64(imageUri: Uri): String {
    val inputStream = contentResolver.openInputStream(imageUri)
    val imageBytes = inputStream?.readBytes()
    inputStream?.close()

    return Base64.encodeToString(imageBytes, Base64.DEFAULT)

}*/


/*

    private fun selectCertificate(view:View){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        permissionLauncherImage.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionLauncherImage.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncherImage.launch(intentToGallery)
        }
    }


    private fun registerCertificate(){
        activityResultLauncherImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPictureImage = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                this.contentResolver,
                                selectedPictureImage!!
                            )
                            selectedBitmapImage = ImageDecoder.decodeBitmap(source)
                            this.courseImage.setImageBitmap(selectedBitmapImage)
                        } else {
                            selectedBitmapImage = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                selectedPictureImage
                            )
                            this.courseImage.setImageBitmap(selectedBitmapImage)
                        }
                        val cervicatePath = selectedPictureImage?.path
                        setImageUrl.setText(cervicatePath!!.substring((cervicatePath.length - 8) , cervicatePath.length)+".JPG")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncherImage = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncherImage.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@CourseUploadActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }*/

/* val intent = Intent(Intent.ACTION_GET_CONTENT)
 intent.type = "image/*"
 intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
 startActivityForResult(intent, REQUEST_CODE_GALLERY)
 leftArrow.visibility = View.VISIBLE
 rightarrow.visibility = View.VISIBLE
 */
 */
/*

        val courseImageDrawable = this.courseImage.drawable
        if (courseImageDrawable != null) {
            val bitmapC = (courseImageDrawable as BitmapDrawable).bitmap
            val streamC = ByteArrayOutputStream()
            bitmapC.compress(Bitmap.CompressFormat.JPEG, 100, streamC)
            val byteArrayC = streamC.toByteArray()
            val encodedStringC = Base64.encodeToString(byteArrayC, Base64.DEFAULT)
            println(encodedStringC)
        } else {
            println("courseImage drawable is null")
        }

*/
/*        if(position < items.size && position < items[position].announcemenets.size && favList.contains(items[position].announcemenets[position].id)){
        holder.heartButton.setBackgroundResource(R.drawable.favorite_for_product)
            addedToFav=true
            println("addedToFav: $addedToFav")
            return
        }
        else{
            holder.heartButton.setBackgroundResource(R.drawable.favorite_border_for_product)
            println("addedToFav: $addedToFav")

        }

        holder.heartButton.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < items.size) {
                if (addedToFav) {
                    heartlistener.onHeartItemCLick(items[currentPosition], addedToFav, currentPosition)
                    addedToFav = false
                    holder.heartButton.setBackgroundResource(R.drawable.favorite_border_for_product)
                } else {
                    heartlistener.onHeartItemCLick(items[currentPosition], addedToFav, currentPosition)
                    addedToFav = true
                    holder.heartButton.setBackgroundResource(R.drawable.favorite_for_product)
                }

            }

        }*/
//FavBtn
//position < items.size && position < items[position].announcemenets.size && favList.contains(items[position].announcemenets[position].id)
//favList.contains(items[position].announcemenets.get(position).id)
/*
        if (item.isSelected) {
            holder.heartButton.setImageResource(R.drawable.favorite_for_product)
        } else {
            holder.heartButton.setImageResource(R.drawable.favorite_border_for_product)
        }
        holder.heartButton.setOnClickListener {
            if (item.isSelected) {
                selectedItemsList.remove(item)
            } else {
                selectedItemsList.add(item)
            }

            // Toggle the favorite status
            item.isSelected = !item.isSelected

            // Update the UI of the heart drawable file
            if (item.isSelected) {
                holder.heartButton.setImageResource(R.drawable.favorite_for_product)
            } else {
                holder.heartButton.setImageResource(R.drawable.favorite_border_for_product)
            }
        }
*/