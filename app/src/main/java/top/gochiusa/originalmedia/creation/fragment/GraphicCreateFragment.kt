package top.gochiusa.originalmedia.creation.fragment


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_creat_graphic.*
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.base.BaseFragment

import top.gochiusa.originalmedia.util.Constant


class GraphicCreateFragment : BaseFragment() {


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


    private val mRoundedCorners: RoundedCorners = RoundedCorners(10)//圆角为5
    private val mOptions = RequestOptions.bitmapTransform(mRoundedCorners);

    private fun init() {
        Glide.with(this)
            .load(R.drawable.pic_add)
            .apply(mOptions)
            .into(iv_add_pic);
        iv_add_pic.setOnClickListener {

            openSystemImageChooser(REQUEST_CODE_PICK)
        }

        ll_graphic_finish.setOnClickListener{
            if (Constant.LOGIN_USER_ID == 0){
                toast("请登录再发布文章")
                return@setOnClickListener
            }
            else{

            }
        }
    }

    /**
     * 使用 ACTION_PICK 选择图片，启动 Activity Intent
     */
    private fun openSystemImageChooser(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
        // 选择视频: intent.type = "video/*";
        // 选择所有类型的资源: intent.type = "*/*"
    }


    /**
     * 在返回的 onActivityResult 中接收选取返回的图片资源
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            Log.d(TAG, "onActivityResult not ok.")
            return
        }

        if (requestCode == REQUEST_CODE_PICK) {
            // 获取选取返回的图片资源, Uri 格式
            val uri = data?.data ?: return

            // URI 格式参考: content://media/external/images/media/123
            Log.d(TAG, "选取的图片: $uri")



            Glide.with(this)
                .load(uri)
                .apply(mOptions)
                .into(iv_add_pic);

            iv_add_pic.scaleType = ImageView.ScaleType.CENTER
            // 查询图片的详细信息
            queryUriDetail(uri)
        }
    }

    private fun queryUriDetail(uri: Uri) {
        // 如果需要选取的图片的详细信息（图片大小、路径、所在相册名称、修改时间、MIME、宽高、文件名等）,
        // 则需要通过 content.getContentResolver().query(uri, ...) 查询（直接查询所有字段）
        val cursor = activity?.contentResolver?.query(uri, null, null, null, null)

        // 一般查询出来的只有一条记录
        if (cursor?.moveToFirst() == true) {
            // 查看查询结果数据的的所有列, 不同系统版本列名数量和类型可能不相同, 参考:
            // [_id, _data, _size, _display_name, mime_type, title, date_added, date_modified,
            // description, picasa_id, isprivate, latitude, longitude, datetaken, orientation,
            // mini_thumb_magic, bucket_id, bucket_display_name, width, height]

            // 获取图片的 大小、文件名、路径
            // val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE))
            // val filename = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
            // val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))

            // 输出所有列对应的值
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





}

