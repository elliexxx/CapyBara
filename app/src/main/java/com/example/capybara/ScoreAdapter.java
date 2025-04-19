package com.example.capybara;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<Score> scoreList;

    public ScoreAdapter(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        Score score = scoreList.get(position);
        holder.nameText.setText(score.getPlayerName());
        holder.scoreText.setText("Score: " + score.getScore());
        holder.timestampText.setText(score.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, scoreText, timestampText;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_player_name);
            scoreText = itemView.findViewById(R.id.text_score);
            timestampText = itemView.findViewById(R.id.text_timestamp);
        }
    }
}
