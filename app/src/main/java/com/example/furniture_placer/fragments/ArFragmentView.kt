package com.example.furniture_placer.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.camera.CameraService.StorageService
import com.example.furniture_placer.R
import com.example.furniture_placer.interfaces.ModelChangeCommunicator
import com.example.furniture_placer.data_models.Furniture
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.services.FirebaseService
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Sun
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar_fragment_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.Serializable

class ArFragmentView : AppCompatActivity(),
    ModelChangeCommunicator {

    private lateinit var arFrag: ArFragment
    private var modelRenderable: ModelRenderable? = null
    private val models = arrayListOf<Furniture>()
    var uri = Uri.parse("file:///android_asset/models/ikea_stool.gltf")
    private lateinit var editedRoom: Room
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
        openMenuBtn.setOnClickListener {
            Log.d("FYI", "menu button pressed")
            if (isOpen){
                historyModelBtn.visibility = View.VISIBLE
                newModelBtn.visibility = View.VISIBLE
                historyModelText.visibility = View.VISIBLE
                newModelTxt.visibility = View.VISIBLE

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

                newModelBtn.setOnClickListener(null)
                isOpen = true
            }
        }

        addModel.setOnClickListener {
            Log.d("FYI", "new model added")
            add3dObject()
        }

        arFrag = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        //val uri = Uri.parse("file:///android_asset/models/ikea_stool.gltf")
        //https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf

    }

     private fun setModel(){
         Log.d("FYI", "setting Model ${uri}")
        val renderableFuture = ModelRenderable.builder()
            .setSource(this, RenderableSource.builder().setSource(this,
                uri, RenderableSource.SourceType.GLTF2)
                .setScale(0.1f)// Scale the original to 20%
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build())
            .setRegistryId("CesiumMan").build()
        renderableFuture.thenAccept { modelRenderable = it }
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
            val pt = getScreenCenter()
            val hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())

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

                    mNode.setOnTapListener(){ hitTestResult: HitTestResult, motionEvent: MotionEvent ->

                        deleteModelbtn.visibility = View.VISIBLE
                        Log.d("FYI", "Listener added")

                        deleteModelbtn.setOnClickListener {
                            removeAnchorNode(anchorNode)
                            Log.d("FYI", "Model removed")
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
            setModel()
        }else{
            val UriList = arrayListOf<Uri>()
            furniture.modelFiles?.forEach { UriList.add(StorageService().loadModel(furniture.path, it,applicationContext)) }
            uri = UriList[0]
            setModel()
        }
        if(editedRoom.recentFurniture != null) {
            if (!editedRoom.recentFurniture?.contains(furniture.name)!!) {
                editedRoom.recentFurniture?.add(furniture.name)
                FirebaseService().updateRoom(editedRoom)
            }
        }
    }

}