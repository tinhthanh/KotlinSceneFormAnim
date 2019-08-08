package com.merito.kotlinsceneformanim

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var anchorNode: AnchorNode? = null
    private var animator: ModelAnimator? = null
    private var nextAnimation: Int = 0;
    private var animationGrab: ModelRenderable? = null
    private var transformNode: TransformableNode? = null
    private var arFragment: ArFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        arFragment!!.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            if (animationGrab != null) {
                val anchor = hitResult.createAnchor()
                if (anchorNode == null) {
                    anchorNode = AnchorNode(anchor)
                    anchorNode!!.setParent(arFragment!!.arSceneView.scene)
                    transformNode = TransformableNode(arFragment!!.transformationSystem)
                    transformNode!!.scaleController.minScale = 0.09f
                    transformNode!!.scaleController.maxScale = 0.1f
                    transformNode!!.setParent(anchorNode)
                    transformNode!!.renderable = animationGrab
                }
            }
        }
        arFragment!!.arSceneView.scene.addOnUpdateListener {

            if (anchorNode == null) {
                if (animate.isEnabled) {
                    animate.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.GRAY)
                    animate.isEnabled = false
                } else {
                    if (!animate.isEnabled) {
                        animate.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
                        animate.isEnabled = true
                    }
                }
            }
        }
        animate.isEnabled = false
        animate.setOnClickListener {
            if (animator == null || animator!!.isRunning) {
                val data = animationGrab!!.getAnimationData(nextAnimation)
                nextAnimation = (nextAnimation + 1) % animationGrab!!.animationDataCount
                animator = ModelAnimator(data, animationGrab)
                animator!!.start()
            }
        }
        setupModel()
    }

    private fun setupModel() {
        ModelRenderable.builder().setSource(this, R.raw.cangrejo).build()
            .thenAccept { modelRenderable -> animationGrab = modelRenderable }
            .exceptionally { throwable ->
              Toast.makeText(this@MainActivity, ""+throwable.message, Toast.LENGTH_SHORT).show()
              null
            }
    }
}
