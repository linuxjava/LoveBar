package xiao.love.bar.component.photopick;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import xiao.love.bar.R;
import xiao.love.bar.component.imageloader.ImageLoadTool;

/**
 * Created by chenchao on 15/5/6.
 */
class GridPhotoAdapter extends CursorAdapter {

    LayoutInflater mInflater;
    PhotoPickActivity mActivity;

    GridPhotoAdapter(Context context, Cursor c, boolean autoRequery, PhotoPickActivity activity) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
        mActivity = activity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = mInflater.inflate(R.layout.photopick_gridlist_item, parent, false);
        convertView.getLayoutParams().height = context.getResources().getDisplayMetrics().widthPixels / 3;

        GridViewHolder holder = new GridViewHolder();
        holder.icon = (ImageView) convertView.findViewById(R.id.icon);
        holder.iconFore = (ImageView) convertView.findViewById(R.id.iconFore);
        holder.check = (CheckBox) convertView.findViewById(R.id.check);
        PhotoPickActivity.GridViewCheckTag checkTag = new PhotoPickActivity.GridViewCheckTag(holder.iconFore);
        holder.check.setTag(checkTag);
        holder.check.setOnClickListener(mClickItem);
        convertView.setTag(holder);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        GridViewHolder holder;
        holder = (GridViewHolder) view.getTag();

        String path = ImageInfo.pathAddPreFix(cursor.getString(1));
        ImageLoadTool.getInstance().displayImage(holder.icon, path, ImageLoadTool.getInstance().optionsPhotoPick, null);

        ((PhotoPickActivity.GridViewCheckTag) holder.check.getTag()).path = path;

        boolean picked = mActivity.isPicked(path);
        holder.check.setChecked(picked);
        holder.iconFore.setVisibility(picked ? View.VISIBLE : View.INVISIBLE);
    }

    static class GridViewHolder {
        ImageView icon;
        ImageView iconFore;
        CheckBox check;
    }

    View.OnClickListener mClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.clickPhotoItem(v);
        }
    };
}
