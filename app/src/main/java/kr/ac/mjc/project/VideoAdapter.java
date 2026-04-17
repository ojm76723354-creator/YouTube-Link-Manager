package kr.ac.mjc.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoItem> videoItems = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(VideoItem video);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(VideoItem video);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem video = videoItems.get(position);
        holder.tvTitle.setText(video.getTitle());
        holder.tvUrl.setText(video.getUrl());
        holder.tvMemo.setText(video.getMemo());
        holder.tvCategory.setText(video.getCategory());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(video);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) longClickListener.onItemLongClick(video);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    public void setVideos(List<VideoItem> videos) {
        this.videoItems = videos;
        notifyDataSetChanged();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUrl, tvMemo, tvCategory;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvUrl = itemView.findViewById(R.id.tv_url);
            tvMemo = itemView.findViewById(R.id.tv_memo);
            tvCategory = itemView.findViewById(R.id.tv_category);
        }
    }
}
