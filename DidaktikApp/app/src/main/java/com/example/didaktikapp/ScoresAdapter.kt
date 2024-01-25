package com.example.didaktikapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScoresAdapter : RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder>() {

    private var scoresList: List<Score> = ArrayList()

    fun submitList(scores: List<Score>) {
        scoresList = scores
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(scoresList[position])
    }

    override fun getItemCount(): Int {
        return scoresList.size
    }

    class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)

        fun bind(score: Score) {
            nameTextView.text = score.name
            scoreTextView.text = score.score.toString()
        }
    }
}
