package com.example.furniture_placer.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.example.camera.CameraService.StorageService
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.DecorationSnapshot
import com.example.furniture_placer.data_models.Furniture
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.interfaces.ModelChangeCommunicator
import com.example.furniture_placer.services.FirebaseService
import com.google.ar.core.Plane
import com.google.ar.sceneform.*
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_ar_fragment_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ArFragmentView : AppCompatActivity(),
    ModelChangeCommunicator {

    private lateinit var arFrag: ArFragment
    private var modelRenderable: ModelRenderable? = null
    private val models = arrayListOf<Furniture>()
    var uri = Uri.parse("")
    private lateinit var editedRoom: Room
    private lateinit var currentFurniture : Furniture
    private var selectedFurnitures = ArrayList<Furniture>()
    var id = 0
    var selectedFurniture: Furniture? = null
    val addedItemsInScene : ArrayList<Furniture> = ArrayList<Furniture>()
    //private var communicator: Communicator

    private var isOpen: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val room = intent.getParcelableExtra<Room>("EDITED_ROOM")
        if (room != null) {
            editedRoom = room
        }


        setContentView(R.layout.activity_ar_fragment_view)
        listFiles()
        setModel()

        arFrag = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Here you can start decorating your room. By taking a screenshot you can save the session.")
            setTitle("Info")
            setPositiveButton("OK"){ dialog, which ->
                //ActivityCompat.requestPermissions(this@ArFragmentView, arrayOf(permission),requestCode)
            }
            val dialog = builder.create()
            dialog.show()
        }

        openMenuBtn.setOnClickListener {
            Log.d("FYI", "menu button pressed")
            if (isOpen){
                historyModelBtn.visibility = View.VISIBLE
                newModelBtn.visibility = View.VISIBLE
                historyModelText.visibility = View.VISIBLE
                newModelTxt.visibility = View.VISIBLE
                screenshotBtn.visibility = View.VISIBLE
                screenshotTxt.visibility = View.VISIBLE

                newModelBtn.setOnClickListener {
                    val dialog =
                        NewModelDialog(
                            models
                        )
                    dialog.show(supportFragmentManager, "chooseModelDialog")
                }
                isOpen = false
            } else {
                historyModelBtn.visibility = View.GONE
                newModelBtn.visibility = View.GONE
                historyModelText.visibility = View.GONE
                newModelTxt.visibility = View.GONE
                screenshotBtn.visibility = View.GONE
                screenshotTxt.visibility = View.GONE

                newModelBtn.setOnClickListener(null)
                isOpen = true
            }
        }

        //Screenshot button action, saves image to firebase and stores snapshot information to scene
        screenshotBtn.setOnClickListener {
            Log.d("FYI", "screenshot of ar view")
            var mCurrentPhotoPath: String = ""
            val view: ArSceneView = arFrag.arSceneView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            PixelCopy.request(view, bitmap, { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    //time stamp
                    val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd_HH:mm:ss")
                    val currentDateAndTime: String = simpleDateFormat.format(Date())

                    val imagePath = "${FirebaseService().getCurrentUser()?.uid}/${editedRoom.name}/${currentDateAndTime}.jpg"
                    val baos = ByteArrayOutputStream()
                    val resizedImage = Bitmap.createScaledBitmap(bitmap, 1080, 1920, false)
                    resizedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                    val data = baos.toByteArray()

                    GlobalScope.launch(Dispatchers.Main) {
                        StorageService().storePicture(BitmapFactory.decodeByteArray(data, 0, data.size), imagePath)
                    }

                    val furnitures = addedItemsInScene
                    editedRoom.decorationSnapshots?.add(DecorationSnapshot(name = currentDateAndTime,photoPath = imagePath,itemsInScene = furnitures))
                    FirebaseService().updateRoom(editedRoom)
                    Log.d("FYI", "saved image")

                    for (furniture in selectedFurnitures){
                        editedRoom.recentFurniture?.add(furniture)
                    }
                    FirebaseService().updateRoom(editedRoom)

                    /*if(editedRoom.recentFurniture != null) {
                        if (!editedRoom.recentFurniture?.contains(furniture.name)!!) {
                            editedRoom.recentFurniture?.add(furniture.name)
                            FirebaseService().updateRoom(editedRoom)
                        }
                    }*/

                    Toast.makeText(applicationContext, "Screenshot taken", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("FYI", "Pixel copy did not succeed")
                }
            }, Handler(Looper.getMainLooper()))
        }


        //val uri = Uri.parse("file:///android_asset/models/ikea_stool.gltf")
        //https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf

    }

     private fun setModel(){
         Log.d("FYI", "setting Model ${uri} and ${modelRenderable}")
        val renderableFuture = ModelRenderable.builder()
            .setSource(this, RenderableSource.builder().setSource(this,
                uri, RenderableSource.SourceType.GLTF2)
                .setScale(1f)// Scale the original to 20%
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build())
            .setRegistryId("id${id++}").build()
         renderableFuture.thenAccept{
             modelRenderable = it
             Log.d("FYI", "model renderable changed")

             addModel.setOnClickListener {
                 Log.d("FYI", "new model added")
                 add3dObject()
             }
             Log.d("MODEL", "model is now changed")
         }
        renderableFuture.exceptionally {// something went wrong notify
            Log.e("FYI", "renderableFuture error: ${it.localizedMessage}")
            null
        }
    }



    private fun getScreenCenter(): Point {
        val vw = findViewById<View>(android.R.id.content)

        return Point(vw.width / 2, vw.height / 2)
    }

    private fun add3dObject() {
        val frame = arFrag.arSceneView.arFrame
        if (frame != null && modelRenderable != null) {
            Log.d("MODEL", modelRenderable.toString())
            val pt = getScreenCenter()
            val hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            // Adding item in to sceneItems array
            selectedFurniture?.let { addedItemsInScene.add(it) }

            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane) {
                    val anchor = hit!!.createAnchor()
                    var anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFrag.arSceneView.scene)
                    val mNode = TransformableNode(arFrag.transformationSystem)
                    mNode.setParent(anchorNode)
                    mNode.renderable = modelRenderable
                    mNode.select()

                    //furniture data saved here
                    //used in click listener to remove right model
                    val selectedItem = selectedFurniture
                    mNode.setOnTapListener(){ hitTestResult: HitTestResult, motionEvent: MotionEvent ->

                        deleteModelbtn.visibility = View.VISIBLE

                        Log.d("FYI", "Listener added: $selectedItem")

                        deleteModelbtn.setOnClickListener {
                            //val itemType = selectedFurniture
                            //val json = Gson().toJson(selectedFurniture)
                            //val newFurniture = Gson().fromJson(json, Furniture::class.java)
                            addedItemsInScene.remove(selectedItem)
                            if (selectedFurnitures.contains(selectedItem)){
                                selectedFurnitures.remove(selectedItem)
                            }
                            removeAnchorNode(anchorNode)
                            Log.d("FYI", "Model removed ${selectedFurnitures}")
                            deleteModelbtn.visibility = View.GONE
                        }
                    }
                    break
                }
            }
        }
    }

    private fun removeAnchorNode(nodeRemove: Node) {

        //Remove an anchor node
        if (nodeRemove is AnchorNode) {
            if (nodeRemove.anchor != null) {
                nodeRemove.anchor!!.detach()
            }
        }
        if (nodeRemove !is Camera && nodeRemove !is Sun) {
            nodeRemove.setParent(null)
        }
    }

    private fun listFiles() {
        GlobalScope.launch(Dispatchers.Main) {
            val furnitures = FirebaseService().getFurnitures()
            Log.d("ARF", "furnitures $furnitures")
            furnitures.forEach { if(it != null){
                models.add(it)}
            }
        }
    }

    override suspend fun changeModel(furniture: Furniture) {
        val file = File("${applicationContext.filesDir}/models/${furniture.modelFiles?.get(0)}")
        if (file.exists()) {
            //file exists
            uri = file.toUri()
            modelRenderable = null
            setModel()
        }else{
            val UriList = arrayListOf<Uri>()
            furniture.modelFiles?.forEach { UriList.add(StorageService().loadModel(furniture.path, it,applicationContext)) }
            uri = UriList[0]
            modelRenderable = null
            setModel()
        }
        if (!editedRoom.recentFurniture?.contains(furniture)!!){
            if (editedRoom.recentFurniture != null){
                selectedFurnitures.add(furniture)
            }
        }

        /*if(editedRoom.recentFurniture != null) {
            if (!editedRoom.recentFurniture?.contains(furniture.name)!!) {
                editedRoom.recentFurniture?.add(furniture.name)
                FirebaseService().updateRoom(editedRoom)
            }
        }*/
        selectedFurniture = furniture
    }

}