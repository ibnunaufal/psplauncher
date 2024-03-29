package id.co.solusinegeri.psplauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.common.io.Resources

class MenuAdapter(val listMenu: ArrayList<Menus>): RecyclerView.Adapter<MenuAdapter.ListViewHolder>() {
    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var name: TextView = itemView.findViewById(R.id.name)
        var icon: ImageView = itemView.findViewById(R.id.icon)
        var entire: LinearLayout = itemView.findViewById(R.id.entire)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapter.ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_menus, parent, false)
        view.isFocusable = true
        return ListViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MenuAdapter.ListViewHolder, position: Int) {
        val menu = listMenu[position]
        holder.name.text = menu.name
        holder.icon.setImageDrawable(menu.icon)

        holder.itemView.setOnLongClickListener {
            var context: Context = holder.itemView.context
            context as MainActivity
            context.openAppDetail(menu.label)
            true
        }
        holder.itemView.setOnClickListener {
            when (menu.name) {
                "Tambah" -> {
                    var context: Context = holder.itemView.context
                    context as MainActivity
                    context.addOtherApk()
                }
                "Hapus" -> {
                    var context: Context = holder.itemView.context
                    context as MainActivity
//                    context.showToRemove()
                }
                else -> {
                    val context: Context = holder.itemView.context
                    val i: Intent? = context.packageManager.getLaunchIntentForPackage(menu.label)
                    context.startActivity(i)
                }
            }
        }
        holder.itemView.isFocusable = true
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
//                holder.itemView.setBackgroundColor(Color.parseColor("#A8A0A1A3"))
//                holder.itemView.background = holder.itemView.context.getDrawable(R.drawable.active_new)
                holder.entire.background = holder.itemView.context.getDrawable(R.drawable.active_new)

            }else{
//                holder.itemView.setBackgroundColor(Color.parseColor("#00FFFFFF"))
                holder.entire.setBackgroundColor(Color.parseColor("#00FFFFFF"))
            }
        }

    }

    override fun getItemCount(): Int {
        return listMenu.size
    }
}