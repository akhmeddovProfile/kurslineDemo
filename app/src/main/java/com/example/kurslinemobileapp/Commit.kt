package com.example.kurslinemobileapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.view.View
import android.widget.RelativeLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.kurslinemobileapp.adapter.PhotoPagerAdapter
import com.example.kurslinemobileapp.model.uploadPhoto.SelectionPhotoShowOnViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
//import kotlinx.android.synthetic.main.activity_update_announcement.*
import java.io.ByteArrayOutputStream
import java.io.InputStream

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
/*override fun onHeartItemCLick(heart: GetAllAnnouncement, liked: Boolean,position:Int,) {

    compositeDisposable= CompositeDisposable()


    val annId = sharedPreferences.getInt("announcementId",0)
    val userId = sharedPreferences.getInt("userID",0)
    val token = sharedPreferences.getString("USERTOKENNN","")
    val authHeader = "Bearer $token"
    println("gelenid" + annId)
    println("userid" + userId)
    println("token:"+authHeader)

    println("Clicked")
    val item=heart.announcemenets.get(position).id
    favModel= SendFavModel(item,userId,authHeader)

    var likeState = liked
    println("likeState: $liked")
    if (likeState){
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
        compositeDisposable.add(
            retrofit.postFavorite(token!!,userId,favModel.productid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                        println("Handler Response: "+it.isSuccess)
                           likeState=true
                           },{ throwable ->
                    println("MyTests: $throwable")
                })
        )
    }
    else{

    }
}
*/
/* val adapter = mainListProductAdapter as? MainListProductAdapter
      adapter?.notifyDataSetChanged()
      if(item.isSelected){
          favList.add(item.copy(isSelected = isSelected))
      }else{
          favList.remove(item.copy(isSelected = isSelected))
       }
      favModel=SendFavModel(userId,annId,isSelected = isSelected)
      println("FavModel: "+favModel)
      var likeState = isSelected
      println("likeState: $likeState")
      if (likeState){
          val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
          compositeDisposable.add(
              retrofit.postFavorite(token!!,favModel.userid,annId)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe({
                      println("Handler Response: "+it.isSuccess)
                      likeState=true
                  },{ throwable ->
                      println("MyTests: $throwable")
                  })
          )
      }
      else{

      }*/
/*
    override fun onFavoriteItemClick(item: SendFavModel,liked:Boolean) {
        compositeDisposable= CompositeDisposable()
        val adapter = mainListProductAdapter as? MainListProductAdapter
        adapter?.notifyDataSetChanged()
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("gelenid" + annId)
        println("userid" + userId)
        println("token:"+authHeader)
        val favoriteproduct=item.productid
        favModel= SendFavModel(userId,favoriteproduct,liked)
        var likeState1 = liked
        println("likeState: $liked")

        if(likeState1){
            val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
            compositeDisposable.add(
                retrofit.deleteFavorite(token!!,favModel.userid,favModel.productid).
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe({
                            likeState1=false
                        },{throwable->
                            println("My msg: ${throwable}")
                        })
            )
        }else{
            val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
            compositeDisposable.add(
                retrofit.postFavorite(token!!,favModel.userid,annId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        println("Handler Response: "+it.isSuccess)
                        likeState1=true
                    },{ throwable ->
                        println("MyTests: $throwable")
                    })
            )
        }



    }*/



/*

fun showBottomSheetDialog() {
    val dialog = BottomSheetDialog(requireContext())
    dialog.setContentView(R.layout.sorted_layout)
    val btnAtoZ = dialog.findViewById<RelativeLayout>(R.id.rl_atoz)
    val btnZtoA = dialog.findViewById<RelativeLayout>(R.id.rl_ztoa)

    btnAtoZ?.setOnClickListener {
        mainList2.clear()
        mainList2.addAll(mainList.sortedBy { it.announcemenets.firstOrNull()?.announcementName })
        println("a to z: "+mainList2)
        mainListProductAdapter.notifyDataSetChanged()
        dialog.dismiss()
    }
    btnZtoA?.setOnClickListener {
        mainList2.clear()
        mainList2.addAll(mainList.sortedByDescending {  it.announcemenets.firstOrNull()?.announcementName })
        println(mainList2)
        println(mainList)
        mainListProductAdapter.notifyDataSetChanged()
        dialog.dismiss()
    }
    dialog.show()
}

fun showBottomSheetDialogPrice() {
    val dialog = BottomSheetDialog(requireContext())
    dialog.setContentView(R.layout.sorted_layout_price)
    val btnMinMax = dialog.findViewById<RelativeLayout>(R.id.rl_minmax)
    val btnmaxmin = dialog.findViewById<RelativeLayout>(R.id.rl_maxmin)

    btnMinMax?.setOnClickListener {
        mainList2.clear()
        mainList2.addAll(mainList.sortedByDescending { it.announcemenets.firstOrNull()?.price })
        println(mainList2)
        println(mainList2)
        mainListProductAdapter.notifySetChanged(mainList2)
        dialog.dismiss()
    }
    btnmaxmin?.setOnClickListener {
        mainList2.clear()
        mainList2.addAll(mainList.sortedBy {  it.announcemenets.firstOrNull()?.price })
        println(mainList2)
        println(mainList)
        mainListProductAdapter.notifySetChanged(mainList2)
        dialog.dismiss()
    }
    dialog.show()
}*/
/*     val miuiVersion = BuildProperties.getMIUIVersion()
      if (miuiVersion == "14.0.3") {
          showMIUIExplanationDialog() // Show a custom explanation
      }*/
/*
    object BuildProperties {
        private val miuiVersionRegex = Regex("V(\\d+\\.\\d+\\.\\d+)")

        fun getMIUIVersion(): String? {
            val properties = System.getProperties()
            val versionProp = properties.getProperty("ro.miui.ui.version.name", "")
            val matchResult = miuiVersionRegex.find(versionProp)
            return matchResult?.groups?.get(1)?.value
        }
    }
*/

/*   private fun requestPermission() {
       if (SDK_INT >= Build.VERSION_CODES.R) {
           try {
               val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
               intent.addCategory("android.intent.category.DEFAULT")
               intent.data =
                   Uri.parse(java.lang.String.format("package:%s", this.getPackageName()))
               requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
               //requestPermissionIntent.launch(intent)
           } catch (e: Exception) {
               val intent = Intent()
               intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
               //requestPermissionIntent.launch(intent)
               requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)

           }
       } else {
           //below android 11
        *//*   ActivityCompat.requestPermissions(
                (this as Activity?)!!,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                80
            )*//*
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(WRITE_EXTERNAL_STORAGE),
                    80
                )
            } else {
                openGallery()
            }

        }
    }*/
/*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, proceed with the intent
                launchGalleryIntent()
            } else {
                // Permission was denied, handle accordingly (e.g., show a message)
            }
        }
    }
*//*    private fun showMIUIExplanationDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Gallery Access")
        dialogBuilder.setMessage("In MIUI 14.0.3, you need to manually grant gallery access. Please follow the steps in the next screen.")
        dialogBuilder.setPositiveButton("Continue") { _, _ ->
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
        }
        dialogBuilder.setCancelable(true)
        val dialog = dialogBuilder.create()
        dialog.show()
    }*/
/* val permissions = arrayOf(
     READ_EXTERNAL_STORAGE,
     WRITE_EXTERNAL_STORAGE
 )

 val hasReadPermission = ContextCompat.checkSelfPermission(
     this@RegisterCompanyActivity,
     READ_EXTERNAL_STORAGE
 ) == PackageManager.PERMISSION_GRANTED

 val hasWritePermission = ContextCompat.checkSelfPermission(
     this@RegisterCompanyActivity,
     WRITE_EXTERNAL_STORAGE
 ) == PackageManager.PERMISSION_GRANTED

 if (hasReadPermission && hasWritePermission) {
     // Permissions are already granted, proceed with the intent
     launchGalleryIntent()
 } else {
     // Permissions are not granted, request them
     ActivityCompat.requestPermissions(
         this@RegisterCompanyActivity,
         permissions,
         REQUEST_CODE_PERMISSIONS
     )
 }*/
/*
    private val requestPermissionIntent: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()

            } else if (result.resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
*/
/*   val token = sharedPreferences.getString("USERTOKENNN", "")
   val authHeader = "Bearer $token"
   val userId = sharedPreferences.getInt("userID", 0)
   val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)

   val targetList = if (isVip) vipList else mainList2 // Determine the target list

   if (position >= 0 && position < targetList.size) { // Check if position is within valid range
       compositeDisposable.add(
           retrofit.postFavorite(authHeader, userId, id)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe({ response ->
                   println(response.isSuccess)
                   targetList[position].isFavorite = response.isSuccess
                   mainListProductAdapter.LikedItems(targetList, position)
               }, { throwable ->
                   println("Error: ${throwable.message}")
               })
       )
   } else {
       println("Invalid position: $position")
   }*/
/*

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                val visibleItemCount = layoutManager?.childCount ?: 0
                val totalItemCount = layoutManager?.itemCount ?: 0
                val firstVisibleItemPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE
                ) {
                    // Load more data
                    currentOffset += 1
                    loadMoreData(currentOffset)
                }
            }
        })
*/
/*val adapter = mainListProductAdapter as? MainListProductAdapter
    if (adapter != null) {
        adapter.getItem(position).isFavorite = isFavorite
        adapter.notifyItemChanged(position)
    }*/
/*           println(it.isSuccess)
     println("postFav - id: $id, position: $position, isVip: $isVip")
     val updatedList = if (isVip) vipList else mainList2
     val isSuccess = it.isSuccess
     updatedList[position].isFavorite = isSuccess
     mainListProductAdapter.notifyItemChanged(position)
     mainListProductAdapter.LikedItems(updatedList, position)*/
/*        val isFavorite = if (productRow.isVIP) vipFavorites[productRow.id] ?: false else normalFavorites[productRow.id] ?: false
        if (isFavorite) {
            holder.heartButton.setImageResource(R.drawable.favorite_for_product)
        } else {
            holder.heartButton.setImageResource(R.drawable.favorite_border_for_product)
        }

        // Set a click listener for the heart button
        holder.heartButton.setOnClickListener {
            val productId = productRow.id
            val isVip = productRow.isVIP
            val newFavoriteStatus = !isFavorite

            if (userId!=0){

                // Update the favorite status in the appropriate map
                if (isVip) {
                    updateVipFavoriteStatus(productId, newFavoriteStatus)
                } else {
                    updateNormalFavoriteStatus(productId, newFavoriteStatus)
                }

                // Call the click listener callback
                favoriteItemClickListener.onFavoriteItemClick(
                    productId,
                    position,
                    isVip,
                    newFavoriteStatus
                )
            }
            else{
                Toast.makeText(context, "Please log in!", Toast.LENGTH_SHORT).show()
            }
            }*/
/*
        val uProdCategory = sharedPreferences.getString("productDetailCategory", "") ?: ""
        val uProdSubCategory = sharedPreferences.getString("productDetailSubCategory", "") ?: ""
        val uProdPrice = sharedPreferences.getString("productDetailPrice", "") ?: ""
        val uProdName = sharedPreferences.getString("productDetailName", "") ?: ""
        val uProdDesc = sharedPreferences.getString("productDetailDesc", "") ?: ""
        val uProdRegion = sharedPreferences.getString("productDetailRegion", "") ?: ""
        val uProdMode = sharedPreferences.getString("productDetailMode", "") ?: ""
        val uProdTeacher = sharedPreferences.getString("productDetailTeacher", "") ?: ""
        val uProdAddress = sharedPreferences.getString("productDetailAddress", "") ?: ""
        val jsonImageUrls = intent.getStringExtra("imageUrlsJson")
*/

/* upcourseNameEditText.setText(uProdName)
 upcourseAboutEditText.setText(uProdDesc)
 upcourseTeacherEditText.setText(uProdTeacher)
 upcoursePriceEditText.setText(uProdPrice)
 upcourseAddressEditText.setText(uProdAddress)
 upcourseModeEditText.setText(uProdMode)
 upcourseAllCategoryEditText.setText(uProdCategory)
 courseSubCategoryEditText.setText(uProdSubCategory)
 upcourseRegionEditText.setText(uProdRegion)
         if (jsonImageUrls != null) {
     val gson = Gson()
     val type = object : TypeToken<List<Photo>>() {}.type
     val imageUrls = gson.fromJson<List<Photo>>(jsonImageUrls, type)

     val viewPager: ViewPager2 = findViewById(R.id.viewPagerCourseUpdate)
     val photoAdapter = ProductDetailImageAdapter(imageUrls)
     viewPager.adapter = photoAdapter
 }
 //   businessAccountUpdateNameEditText.setText(userFullName)
 */


/*    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                // Permission denied, handle accordingly
            }
        }
    private val galleryLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            uris.forEach { imageUri ->
                val imageName=getImageName(imageUri)
                convertImageToBase64(imageUri,imageName)
            }
        }*/

/*
private fun requestGalleryPermission() {
    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
}
private fun openGallery() {
    galleryLauncher.launch("image/*")
}

@SuppressLint("Range")
private fun getImageName(imageUri: Uri): String? {
    val cursor = contentResolver.query(imageUri, null, null, null, null)
    val name: String? = cursor?.use {
        if (it.moveToFirst()) {
            val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            displayName
        } else {
            null
        }
    }
    cursor?.close()
    return name
}
private fun convertImageToBase64(imageUri: Uri,imageName:String?) {

    val inputStream = contentResolver.openInputStream(imageUri)

    val compressedBitmap = compressImage(inputStream)
    val targetSize = 2_500_000 // Target size in bytes (2.5 MB)
    var compressionQuality = 100 // Start with maximum quality (minimum compression)
    val byteArrayOutputStream = ByteArrayOutputStream()
    compressedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream)
    while (byteArrayOutputStream.size() > targetSize && compressionQuality > 0) {
        byteArrayOutputStream.reset() // Reset the output stream

        // Reduce the compression quality by 10%
        compressionQuality -= 10

        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream)
    }

    val imageBytes = byteArrayOutputStream.toByteArray()
    val base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT)

    inputStream?.close()

    // Use the base64String as needed
    UpsetImageUrl.setText(imageName?.trim().toString())
    UpsetImageUrl.visibility= View.GONE
    println("Image Name: "+UpsetImageUrl.text.toString()+".JPG")
    imageNames.add(imageName!!)
    selectedPhotos.add(SelectionPhotoShowOnViewPager(imageName,imageUri,base64String))
    imageData.add(base64String)
    setupViewPager()
    print("Base64: "+base64String)

}


private fun compressImage(inputStream: InputStream?): Bitmap {
    val options = BitmapFactory.Options()
    options.inSampleSize = 2 // Adjust the sample size as needed for desired compression

    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
    inputStream?.close()

    return bitmap!!
}
private fun setupViewPager() {
    val viewPager = findViewById<ViewPager2>(R.id.viewPagerCourseUpdate)

    // Create the adapter with the selected photos list
    val adapter = PhotoPagerAdapter(selectedPhotos)
    viewPager.adapter = adapter
 */