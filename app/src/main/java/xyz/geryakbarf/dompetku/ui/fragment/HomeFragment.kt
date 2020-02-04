package xyz.geryakbarf.dompetku.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_home.*
import xyz.geryakbarf.dompetku.R
import xyz.geryakbarf.dompetku.adapter.AdapterSaldo
import xyz.geryakbarf.dompetku.models.SaldoModels
import xyz.geryakbarf.dompetku.utils.SQLiteHelper
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var db: SQLiteHelper
    private var isNotEmpty: Boolean = true
    private lateinit var alertDialog: AlertDialog
    private lateinit var btnSimpan: TextView
    private lateinit var btnCancel: TextView
    private lateinit var input_nama: TextInputLayout
    private lateinit var input_saldo: TextInputLayout
    private lateinit var etNama: EditText
    private lateinit var etSaldo: EditText
    private lateinit var jenis: String
    private var list: ArrayList<SaldoModels> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = SQLiteHelper(view.context)
        btnTambahMasuk.setOnClickListener(this)
        btnTambahKeluar.setOnClickListener(this)
        rvTransaksi.setHasFixedSize(true)

        //Instansiasi Alert DIalog
        alertDialog = AlertDialog.Builder(view.context).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_tambah, null)
        input_nama = dialogView.findViewById(R.id.input_layout_nama)
        input_saldo = dialogView.findViewById(R.id.input_layout_saldo)
        etNama = dialogView.findViewById(R.id.etNama)
        etSaldo = dialogView.findViewById(R.id.etSaldo)
        btnSimpan = dialogView.findViewById(R.id.btnSimpan)
        btnCancel = dialogView.findViewById(R.id.btnBatal)
        initialCheck()
        showData()

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString()
            if (TextUtils.isEmpty(nama)) {
                input_nama.error = "Nama Tidak Boleh Kosong!"
                return@setOnClickListener
            }
            val saldo = etSaldo.text.toString().toInt()
            if (TextUtils.isEmpty(saldo.toString())) {
                input_saldo.error = "Saldo Tidak Boleh Kosong!"
                return@setOnClickListener
            }

            val date = getDate()
            val res = db.isNotEmpty()
            if (res.count == 0) {
                val isInserted = db.addFirstCatatan(nama, saldo, date, jenis)
                if (isInserted) {
                    alertDialog.dismiss()
                    initialCheck()
                    showData()
                    Toast.makeText(view.context, "Data berhasil disimpan", Toast.LENGTH_SHORT)
                        .show()
                } else
                    Toast.makeText(
                        view.context,
                        "Gagal menyimpna data :(",
                        Toast.LENGTH_SHORT
                    ).show()
            } else {
                db.addCatatan(nama, saldo, date, jenis)
                alertDialog.dismiss()
                initialCheck()
                showData()
                Toast.makeText(view.context, "Data berhasil disimpan", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        alertDialog.setView(dialogView)
        //
    }

    private fun initialCheck() {
        val saldo = db.getSaldo()
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        textView.text = format.format(saldo)
    }

    private fun getDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm")
        return sdf.format(Date())
    }

    private fun getToday(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy")
        return sdf.format(Date())
    }

    private fun showData() {
        list.clear()
        val res = db.getTodayNote(getToday())
        if (res.count > 0) {
            while (res.moveToNext()) {
                val dataSaldo: SaldoModels =
                    SaldoModels(res.getString(1), res.getString(0), res.getString(2), res.getInt(3))
                list.add(dataSaldo)
            }
        }
        rvTransaksi.layoutManager = LinearLayoutManager(context)
        rvTransaksi.adapter = AdapterSaldo(list)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btnTambahMasuk -> {
                alertDialog.show()
                jenis = "Pemasukan"
            }
            R.id.btnTambahKeluar -> {
                alertDialog.show()
                jenis = "Pengeluaran"
            }

        }
    }
}