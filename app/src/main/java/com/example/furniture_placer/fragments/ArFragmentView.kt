package com.example.furniture_placer.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.furniture_placer.services.StorageService
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
    private var uri = Uri.parse("")
    private lateinit var editedRoom: Room
    private var id = 0
    private var selectedFurniture: Furniture? = null
    private val addedItemsInScene : ArrayList<Furniture> = ArrayList()


    private var isOpen: Boolean = true
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val room = intent.getParcelableExtra<Room>("EDITED_ROOM")
        if (room != null) {
            editedRoom = room
        }

        setContentView(R.layout.activity_ar_fragment_view)
        listFiles()

        arFrag = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        //user cant press add model button if model is not chosen
        if (modelRenderable === null){
            addModel.isEnabled = false
            addModel.isClickable = false
        }

        openMenuBtn.setOnClickListener {
            Log.d("FYI", "menu button pressed")

            //menu buttons either gone or visible
            //when they are visible clicklisteners are set
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
                historyModelBtn.setOnClickListener {
                    val furnitureHistory = editedRoom.recentFurniture
                    if(furnitureHistory != null) {
                        val safeRecentFurniture : ArrayList<Furniture> = furnitureHistory
                        if (safeRecentFurniture.size < 1){
                            Toast.makeText(applicationContext, getString(R.string.noHistoryToast), Toast.LENGTH_SHORT).show()
                        }
                        else{
                            val dialog = NewModelDialog(safeRecentFurniture)
                            dialog.show(supportFragmentManager, "chooseModelDialog")
                        }
                    }
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
                historyModelBtn.setOnClickListener(null)
                isOpen = true
            }
        }

        //Screenshot button action, saves image to firebase and stores snapshot information to scene
        screenshotBtn.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val view: ArSceneView = arFrag.arSceneView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            builder.setMessage(getString(R.string.screenshot_question))
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        if (addedItemsInScene.isNotEmpty()) {
                            //screenshot taken with PixelCopy
                            PixelCopy.request(view, bitmap, { copyResult ->
                                if (copyResult == PixelCopy.SUCCESS) {
                                    //time stamp
                                    val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd_HH:mm:ss")
                                    val currentDateAndTime: String = simpleDateFormat.format(Date())

                                    val imagePath =
                                        "${FirebaseService().getCurrentUser()?.uid}/${editedRoom.name}/${currentDateAndTime}.jpg"
                                    val baos = ByteArrayOutputStream()
                                    //image resized and compressed to optimize the size of file
                                    val resizedImage =
                                        Bitmap.createScaledBitmap(bitmap, 1080, 1920, false)
                                    resizedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                                    val data = baos.toByteArray()

                                    GlobalScope.launch(Dispatchers.Main) {
                                        StorageService().storePicture(
                                            BitmapFactory.decodeByteArray(
                                                data,
                                                0,
                                                data.size
                                            ), imagePath
                                        )
                                    }

                                    val furnitures = addedItemsInScene
                                    editedRoom.decorationSnapshots?.add(
                                        DecorationSnapshot(
                                            name = currentDateAndTime,
                                            photoPath = imagePath,
                                            itemsInScene = furnitures
                                        )
                                    )
                                    Log.d("FYI", "saved image")

                                    //recent furniture updated
                                    //checked that there are no duplicate mdoels
                                    for (furniture in furnitures) {
                                        if (!editedRoom.recentFurniture?.contains(furniture)!!) {
                                            editedRoom.recentFurniture?.add(furniture)
                                        }
                                    }
                                    FirebaseService().updateRoom(editedRoom)

                                    //ar view reset after screenshot
                                    finish()
                                    overridePendingTransition(0, 0)
                                    startActivity(intent)
                                    overridePendingTransition(0, 0)

                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.screenshot_success),
                                        Toast.LENGTH_SHORT
                                    ).show()


                                } else {
                                    Log.d("FYI", "Pixel copy did not succeed")
                                }
                            }, Handler(Looper.getMainLooper()))
                        }else{
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.screenshot_empty),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
            val alert = builder.create()
            alert.show()

        }

    }

     private fun setModel(){

        val renderableFuture = ModelRenderable.builder()
            .setSource(this, RenderableSource.builder().setSource(this,
                uri, RenderableSource.SourceType.GLTF2)
                .setScale(1f)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build())
            .setRegistryId("id${id++}").build()
         renderableFuture.thenAccept{
             modelRenderable = it

             addModel.setOnClickListener {
                 Log.d("FYI", "new model added")
                 add3dObject()
             }
             addModel.isEnabled = true
             addModel.isClickable = true
         }
        renderableFuture.exceptionally {// something went wrong notify
            Log.e("FYI", "renderableFuture error: ${it.localizedMessage}")
            null
        }
    }


    //screen center calculated in order to put model in center of screen
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
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFrag.arSceneView.scene)
                    val mNode = TransformableNode(arFrag.transformationSystem)
                    mNode.setParent(anchorNode)
                    mNode.renderable = modelRenderable
                    mNode.select()

                    //furniture data saved here
                    //used in click listener to remove right model
                    val selectedItem = selectedFurniture
                    //tap listener to activate delete button
                    mNode.setOnTapListener { _: HitTestResult, _: MotionEvent ->

                        deleteModelbtn.visibility = View.VISIBLE
                        //delete buttonlistener which removes anchor and item from addedItemsInScene
                        deleteModelbtn.setOnClickListener {
                            addedItemsInScene.remove(selectedItem)
                            removeAnchorNode(anchorNode)
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
    //model change logic
    //if model already downloaded same model will be used
    //if not model is loaded from firebase
    override suspend fun changeModel(file: Furniture) {
        val furnitureFile = File("${applicationContext.filesDir}/models/${file.modelFiles?.get(0)}")
        if (furnitureFile.exists()) {
            //file exists
            uri = furnitureFile.toUri()
            modelRenderable = null
            setModel()
        }else{
            val uriList = arrayListOf<Uri>()
            file.modelFiles?.forEach { uriList.add(StorageService().loadModel(file.path, it,applicationContext)) }
            uri = uriList[0]
            modelRenderable = null
            setModel()
        }

        selectedFurniture = file
    }

}