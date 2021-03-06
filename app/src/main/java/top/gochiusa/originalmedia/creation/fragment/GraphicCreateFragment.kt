package top.gochiusa.originalmedia.creation.fragment


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kokomi.uploader.Uploader
import com.kokomi.uploader.entity.ReleaseInfo
import com.kokomi.uploader.listener.UploaderListener
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_creat_graphic.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.base.BaseFragment

import top.gochiusa.originalmedia.util.Constant
import java.io.File


class GraphicCreateFragment : BaseFragment() ,UploaderListener{
    private lateinit var mPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creat_graphic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    companion object {
        private const val TAG = "GraphicCreateFragment"
        private const val REQUEST_CODE_PICK = 1000

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraphicCreateFragment().apply {
                arguments = Bundle().apply {

                }
            }

    }


    private val mRoundedCorners: RoundedCorners = RoundedCorners(10)//?????????5
    private val mOptions = RequestOptions.bitmapTransform(mRoundedCorners);

    private fun init() {
        requestPermissions()
        Glide.with(this)
            .load(R.drawable.pic_add)
            .apply(mOptions)
            .into(iv_add_pic);
        iv_add_pic.setOnClickListener {

            openSystemImageChooser(REQUEST_CODE_PICK)
        }

        ll_graphic_finish.setOnClickListener {
            if (Constant.LOGIN_USER_ID == 0) {
                toast("????????????????????????")
                return@setOnClickListener
            } else {


            lifecycleScope.launch {
                Uploader.uploadImage(
                    ReleaseInfo(
                        Constant.LOGIN_USER_ID.toLong(),
                        tv_create_gra_title.text.toString(),
                        tv_graphic_content.text.toString(),
                        File(mPath)
                    ),this@GraphicCreateFragment
                ).catch {
                    toast("???????????????")
                    it.printStackTrace()
                }.collect{
                    toast("???????????????")
                }
            }


            }
        }
    }

    /**
     * ?????? ACTION_PICK ????????????????????? Activity Intent
     */
    private fun openSystemImageChooser(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
        // ????????????: intent.type = "video/*";
        // ???????????????????????????: intent.type = "*/*"
    }


    /**
     * ???????????? onActivityResult ????????????????????????????????????
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            Log.d(TAG, "onActivityResult not ok.")
            return
        }

        if (requestCode == REQUEST_CODE_PICK) {
            // ?????????????????????????????????, Uri ??????
            val uri = data?.data ?: return

            // URI ????????????: content://media/external/images/media/123
            Log.d(TAG, "???????????????: $uri")



            Glide.with(this)
                .load(uri)
                .apply(mOptions)
                .into(iv_add_pic);

            iv_add_pic.scaleType = ImageView.ScaleType.CENTER
            // ???????????????????????????
            queryUriDetail(uri)
        }
    }

    @SuppressLint("Range")
    private fun queryUriDetail(uri: Uri) {
        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????MIME???????????????????????????,
        // ??????????????? content.getContentResolver().query(uri, ...) ????????????????????????????????????
        val cursor = activity?.contentResolver?.query(uri, null, null, null, null)

        // ???????????????????????????????????????
        if (cursor?.moveToFirst() == true) {
            // ???????????????????????????????????????, ??????????????????????????????????????????????????????, ??????:
            // [_id, _data, _size, _display_name, mime_type, title, date_added, date_modified,
            // description, picasa_id, isprivate, latitude, longitude, datetaken, orientation,
            // mini_thumb_magic, bucket_id, bucket_display_name, width, height]

            // ??????????????? ???????????????????????????
            // val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE))
            // val filename = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))

            mPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))

            // ???????????????????????????
            for (column in cursor.columnNames) {
                val index = cursor.getColumnIndex(column)
                val valueDesc = when (cursor.getType(index)) {
                    Cursor.FIELD_TYPE_NULL -> "$column: NULL"
                    Cursor.FIELD_TYPE_INTEGER -> "$column: " + cursor.getInt(index)
                    Cursor.FIELD_TYPE_FLOAT -> "$column: " + cursor.getFloat(index)
                    Cursor.FIELD_TYPE_STRING -> "$column: " + cursor.getString(index)
                    Cursor.FIELD_TYPE_BLOB -> "$column: BLOB"
                    else -> "$column: Unknown"
                }
                Log.d(TAG, valueDesc)
            }
        }

        cursor?.close()
    }

    override fun onUploading(name: String, progress: Int) {


    }

    // ??????????????????
    private fun requestPermissions() {
        // ????????????????????????????????????????????????????????????????????????????????????
        val permissionList: MutableList<String> = ArrayList()

        // ????????????????????????????????????????????????????????????????????????
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (permissionList.isNotEmpty()) {
            this.activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    permissionList.toTypedArray(), 1002
                )
            }
        } else {
            toast("?????????????????????????????????????????????")
        }
    }
}

