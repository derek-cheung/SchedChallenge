package com.derek.schedchallenge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.derek.schedchallenge.models.Session

class SessionAdapter(
  private val sessionData: List<Session>
) : RecyclerView.Adapter<SessionViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
    return SessionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.session_cell, parent, false))
  }

  override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
    holder.bindData(sessionData[position])
  }

  override fun getItemCount(): Int = sessionData.size
}