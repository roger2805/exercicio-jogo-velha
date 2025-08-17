package com.example.jogo_velha

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var animacaoFundo: LinearLayout
    private lateinit var tabela: GridLayout
    private lateinit var tvTurno: TextView
    private lateinit var tvPontosX: TextView
    private lateinit var tvPontosO: TextView
    private lateinit var btnReiniciar: MaterialButton

    private var turno = "X"
    private var pontosX = 0
    private var pontosO = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        animacaoFundo = findViewById(R.id.main)
        tabela = findViewById(R.id.TabelaJogo)
        tvTurno = findViewById(R.id.JogadorAtual)
        tvPontosX = findViewById(R.id.PontosX)
        tvPontosO = findViewById(R.id.PontosO)
        btnReiniciar = findViewById(R.id.btnReiniciarJogo)

        atualizarTurno()
        animacaoFundo()

        for (i in 0 until tabela.childCount) {
            val button = tabela.getChildAt(i) as MaterialButton
            button.setOnClickListener {
                if (button.text.isEmpty()) {
                    button.text = turno
                    button.setTextColor(if (turno == "X") Color.parseColor("#FF6B6B") else Color.parseColor("#4EC5F1"))
                    animarBotao(button)

                    val vencedor = checarVencedor()
                    if (vencedor != null) {
                        if (vencedor == "X") pontosX++ else pontosO++
                        atualizarPlacar()
                        atualizarPlacar()
                        mostrarAvisoVitoria(vencedor)
                    } else if (checarEmpate()) {
                        mostrarAvisoEmpate()
                    } else {
                        trocarTurno()
                    }
                }
            }
        }

        btnReiniciar.setOnClickListener {
            reiniciarJogo()
        }
    } //fim do oncreate

    private fun animarBotao(button: MaterialButton) {
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f, 1f)
        )
        scaleUp.duration = 200
        scaleUp.start()
    }

    private fun animacaoFundo() {
        val colorFrom = Color.parseColor("#2E2E3A")
        val colorTo = Color.parseColor("#3C3C50")
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 5000
        colorAnimation.repeatMode = ValueAnimator.REVERSE
        colorAnimation.repeatCount = ValueAnimator.INFINITE
        colorAnimation.addUpdateListener { animator ->
            animacaoFundo.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }

    private fun trocarTurno() {
        turno = if (turno == "X") "O" else "X"
        atualizarTurno()
    }

    private fun atualizarTurno() {
        tvTurno.text = "Jogador atual: $turno"
    }

    private fun atualizarPlacar() {
        tvPontosX.text = pontosX.toString()
        tvPontosO.text = pontosO.toString()
    }

    private fun mostrarAvisoVitoria(vencedor: String) {
        val dialogView = layoutInflater.inflate(R.layout.aviso_tela, null)
        val tvMensagem = dialogView.findViewById<TextView>(R.id.tvMensagem)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        tvMensagem.text = "O jogador $vencedor venceu!"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnOk.setOnClickListener {
            dialog.dismiss()
            reiniciarTabuleiro()
        }

        dialog.show()
    }

    private fun mostrarAvisoEmpate() {
        val avisoTela = layoutInflater.inflate(R.layout.aviso_tela, null)
        val tvTitulo = avisoTela.findViewById<TextView>(R.id.tvTitulo)
        val tvMensagem = avisoTela.findViewById<TextView>(R.id.tvMensagem)
        val btnOk = avisoTela.findViewById<Button>(R.id.btnOk)

        tvTitulo.text = "Fim de Jogo"
        tvMensagem.text = "Empate!"

        val dialog = AlertDialog.Builder(this)
            .setView(avisoTela)
            .setCancelable(false)
            .create()

        btnOk.setOnClickListener {
            dialog.dismiss()
            reiniciarTabuleiro()
        }
        dialog.show()
    }

    private fun checarEmpate(): Boolean {
        for (i in 0 until tabela.childCount) {
            val button = tabela.getChildAt(i) as MaterialButton
            if (button.text.isEmpty()) {
                return false
            }
        }
        return true
    }

    private fun checarVencedor(): String? {
        val combinacoes = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )

        for (combo in combinacoes) {
            val b0 = tabela.getChildAt(combo[0]) as MaterialButton
            val b1 = tabela.getChildAt(combo[1]) as MaterialButton
            val b2 = tabela.getChildAt(combo[2]) as MaterialButton

            if (b0.text.isNotEmpty() &&
                b0.text == b1.text &&
                b1.text == b2.text) {
                return b0.text.toString()
            }
        }
        return null
    }

    private fun reiniciarTabuleiro() {
        for (i in 0 until tabela.childCount) {
            val button = tabela.getChildAt(i) as MaterialButton
            button.text = ""
        }
        turno = "X"
        atualizarTurno()
    }

    private fun reiniciarJogo() {
        reiniciarTabuleiro()
        pontosX = 0
        pontosO = 0
        atualizarPlacar()
    }
}// fim do codigo