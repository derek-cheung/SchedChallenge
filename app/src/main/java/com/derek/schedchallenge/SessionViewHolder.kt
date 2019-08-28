package com.derek.schedchallenge

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.derek.schedchallenge.models.Session
import kotlinx.android.synthetic.main.session_cell.view.*

class SessionViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {

  fun bindData(session: Session) {
    itemView.textSessionName.text = session.name
  }
}