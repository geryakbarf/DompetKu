package xyz.geryakbarf.dompetku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.geryakbarf.dompetku.R
import xyz.geryakbarf.dompetku.models.SaldoModels
import java.text.NumberFormat
import java.util.*

class AdapterSaldo(private val list: ArrayList<SaldoModels>) :
    RecyclerView.Adapter<AdapterSaldo.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_transaksi,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (nama, tanggal, jenis, saldo) = list[position]
        holder.txtNama.text = nama
        holder.txtTanggal.text = tanggal
        if (jenis == "Pemasukan")
            holder.imgJenis.setImageResource(R.drawable.down)
        else
            holder.imgJenis.setImageResource(R.drawable.up)
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        holder.txtSaldo.text = format.format(saldo)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtNama: TextView = itemView.findViewById(R.id.txtNamaTransaksi)
        var txtSaldo: TextView = itemView.findViewById(R.id.txtSaldoTransaksi)
        var imgJenis: ImageView = itemView.findViewById(R.id.imgJenisTransaksi)
        var txtTanggal: TextView = itemView.findViewById(R.id.txtTanggalTransaksi)
    }
}