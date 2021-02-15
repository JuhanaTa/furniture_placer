package com.example.furniture_placer

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Point
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar_fragment_view.*

class ArFragmentView : AppCompatActivity(), ModelChangeCommunicator {

    private lateinit var arFrag: ArFragment
    private var modelRenderable: ModelRenderable? = null
    private val models = arrayListOf<OneModel>()
    var uri = Uri.parse("file:///android_asset/models/ikea_stool.gltf")
    //private var communicator: Communicator

    private var isOpen: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_fragment_view)
        listFiles("models")
        setModel()
        openMenuBtn.setOnClickListener {
            Log.d("FYI", "menu button pressed")
            if (isOpen){
                historyModelBtn.visibility = View.VISIBLE
                newModelBtn.visibility = View.VISIBLE
                historyModelText.visibility = View.VISIBLE
                newModelTxt.visibility = View.VISIBLE

                newModelBtn.setOnClickListener {
                    val dialog = NewModelDialog(models)
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

     fun setModel(){
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
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFrag.arSceneView.scene)
                    val mNode = TransformableNode(arFrag.transformationSystem)
                    mNode.setParent(anchorNode)
                    mNode.renderable = modelRenderable
                    mNode.select()
                    break
                }
            }
        }
    }

    private fun listFiles(dirFrom: String) {
        val res: Resources = resources //if you are in an activity
        val am: AssetManager = res.getAssets()
        val fileList = am.list(dirFrom)

        if (fileList != null) {
            for (i in fileList.indices) {
                Log.d("FYI", fileList[i])
                if (fileList[i].takeLast(5) == ".gltf") {
                    models.add(OneModel(fileList[i]))
                }
            }
        }
    }

    override fun changeModel(file: String) {
        uri = Uri.parse(file)
    }
}