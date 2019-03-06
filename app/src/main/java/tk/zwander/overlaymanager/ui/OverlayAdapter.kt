package tk.zwander.overlaymanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import kotlinx.android.synthetic.main.overlay_item.view.*
import tk.zwander.overlaymanager.R
import tk.zwander.overlaymanager.proxy.OverlayInfo
import tk.zwander.overlaymanager.util.app

class OverlayAdapter : RecyclerView.Adapter<OverlayAdapter.OverlayHolder>() {
    private val items = SortedList<OverlayInfo>(OverlayInfo::class.java, object : SortedList.Callback<OverlayInfo>() {
        override fun areItemsTheSame(item1: OverlayInfo?, item2: OverlayInfo?) =
            item1 == item2

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun compare(o1: OverlayInfo, o2: OverlayInfo) =
            o1.packageName.compareTo(o2.packageName)

        override fun areContentsTheSame(oldItem: OverlayInfo, newItem: OverlayInfo) =
            oldItem.packageName == newItem.packageName

    })

    override fun getItemCount() = items.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverlayHolder {
        return OverlayHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.overlay_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OverlayHolder, position: Int) {
        holder.bindInfo(items[position], itemCount)
    }

    fun setItems(list: List<OverlayInfo>) {
        items.clear()

        items.addAll(list)
    }

    class OverlayHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindInfo(info: OverlayInfo, size: Int) {
            itemView.apply {
                val receiver = context.app.receiver

                overlay_package.text = info.packageName

                if (info.isStatic) {
                    enabled.isChecked = true
                    enabled.isEnabled = false
                } else {
                    enabled.isChecked = info.isEnabled

                    enabled.setOnCheckedChangeListener { _, isChecked ->
                        receiver.postAction {
                            it.setOverlayEnabled(info.packageName, isChecked)
                        }
                    }
                }

                updatePriority(info)

                if (size > 1) {
                    set_highest_priority.visibility = View.VISIBLE
                    set_lowest_priority.visibility = View.VISIBLE
                    spacer.visibility = View.VISIBLE

                    set_highest_priority.setOnClickListener {
                        receiver.postAction {
                            it.setOverlayHighestPriority(info.packageName)
                        }
                        updatePriority(info)
                    }

                    set_lowest_priority.setOnClickListener {
                        receiver.postAction {
                            it.setOverlayLowestPriority(info.packageName)
                        }
                        updatePriority(info)
                    }
                } else {
                    set_highest_priority.visibility = View.GONE
                    set_lowest_priority.visibility = View.GONE
                    spacer.visibility = View.GONE
                }
            }
        }

        private fun updatePriority(info: OverlayInfo) {
            itemView.priority.text = itemView.context.resources.getString(R.string.priority, info.priority)
        }
    }
}