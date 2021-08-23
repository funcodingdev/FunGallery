package cn.funcoding.fungallery

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import cn.funcoding.fungallery.adapter.GalleryAdapter
import cn.funcoding.fungallery.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val galleryAdapter = GalleryAdapter { image ->
            Toast.makeText(this, image.displayName, Toast.LENGTH_SHORT).show()
        }

        binding.gallery.also { view ->
            view.layoutManager = GridLayoutManager(this, 3)
            view.adapter = galleryAdapter
        }

        viewModel.images.observe(this) { images ->
            galleryAdapter.submitList(images)
        }

        binding.openAlbum.setOnClickListener {
            openMediaStore()
        }
        binding.grantPermissionButton.setOnClickListener { openMediaStore() }

        if (!haveStorePermission()) {
            binding.welcomeView.visibility = View.VISIBLE
        } else {
            showImages()
        }
    }

    private fun showImages() {
        viewModel.loadImages()
        binding.welcomeView.visibility = View.GONE
        binding.permissionRationaleView.visibility = View.GONE
    }

    private fun showNoAccess() {
        binding.welcomeView.visibility = View.GONE
        binding.permissionRationaleView.visibility = View.VISIBLE
    }

    private fun openMediaStore() {
        if (haveStorePermission()) {
            showImages()
        } else {
            requestPermission()
        }
    }

    private fun haveStorePermission(): Boolean =
        PermissionX.isGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun requestPermission() {
        if (!haveStorePermission()) {
            PermissionX.init(this)
                .permissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .onExplainRequestReason { _, _ ->
                    showNoAccess()
                }
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        getString(R.string.permission_not_granted_need_goto_setting),
                        getString(R.string.submit),
                        getString(R.string.cancel)
                    )
                }
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        showImages()
                    }
                }
        }
    }
}