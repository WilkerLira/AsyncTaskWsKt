package br.edu.ifs.scl.asynctaskwskt

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

import br.edu.ifs.scl.asynctaskwskt.MainActivity.constantes.URL_BASE
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedInputStream
import java.io.IOException
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.URL
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {


    object constantes {
        val URL_BASE = "http://www.nobile.pro.br/sdm/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Seta o Listener para o botão usando uma função lambda
        buscarInformacoesButton.setOnClickListener {
            // Disparando AsyncTask para buscar texto
            val buscarTextoAt = BuscarTextoAt()
            buscarTextoAt.execute(URL_BASE + "texto.php")

            val buscarDataAt = BuscaDataAt()
            buscarDataAt.execute(URL_BASE + "data.php")

        }
    }

    // AsyncTask que fará acesso ao WebService
    private inner class BuscarTextoAt : AsyncTask<String, Int, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            messageToast("Buscando String no WebService")

            //Mostrando barra de progresso
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String?): String {
            //Pegando a URL na primeira posição do params
            val url = params[0]

            // Criando um StringBuffer para receber a resposta do Web Service
            val stringBufferResposta: StringBuffer = StringBuffer()

            // Criando uma conexão HTTP a partir da URL
            //trantando com Try/Cath
            try {
                val conexao = URL(url).openConnection() as HttpURLConnection

                if (conexao.responseCode == HttpURLConnection.HTTP_OK) {
                    // Caso a conexão seja bem sucedida, resgata o InputStream da mesma
                    val inputStream = conexao.inputStream

                    // Cria um BufferedReader a partir do InputStream
                    val bufferedReader = BufferedInputStream(inputStream).bufferedReader()

                    // Lê o bufferedReader para uma lista de Strings
                    val respostaList = bufferedReader.readLines()

                    // "Appenda" cada String da lista ao StringBuffer
                    respostaList.forEach { stringBufferResposta.append(it) }
                }

            } catch (e: IOException) {
                messageToast("Erro na conexão")
            }
            // Simulando notificação do progresso para Thread de UI
            for (i in 1..10) {
                /* Envia um inteiro para ser publicado como progresso. Esse valor é recebido pela função
                   callback onProgressUpdate*/
                publishProgress(i)

                // Dormindo por 0.5 s para simular o atraso de rede
                sleep(500)
            }
            // Retorna a String formada a partir do StringBuffer
            return stringBufferResposta.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            messageToast("Texto recuperado com sucesso")

            //Altera a TextView com o texto recuperado
            textoTextView.text = result

            //Tornado o ProgressBar invisivel
            progressBar.visibility = View.GONE
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            // Se o valor de progresso não for nulo, atualiza a barra de progresso
            values[0]?.apply { progressBar.progress = this }
        }
    }

    // AsyncTask que fará acesso ao WebService
    //Método que busca data.php
    private inner class BuscaDataAt : AsyncTask<String, Void, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            messageToast("Buscando String no WebService")

            //Mostrando barra de progresso
            pb_carregando.visibility = View.GONE
        }

        //TENTANDO FAZER O DOINBACKGROUND
        override fun doInBackground(vararg params: String?): JSONObject {
            val url = params[0]
            val stringBufferResposta: StringBuffer = StringBuffer()
            //Criando conexão Http e trantando no Try/Catch
            try {
                val conexao = URL(url).openConnection() as HttpURLConnection
                if (conexao.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = conexao.inputStream
                    val bufferedReader = BufferedInputStream(inputStream).bufferedReader()
                    val responseList = bufferedReader.readLine()
                    responseList.forEach { stringBufferResposta.append(it) }
                }
                return JSONObject(stringBufferResposta.toString())

            } catch (ioe: IOException) {
                messageToast("Erro de conexão")

            } catch (jsone: JSONException) {
                jsone.printStackTrace()
            }
            // Simulando notificação do progresso para Thread de UI
            for (i in 1..10) {
                /* Envia um inteiro para ser publicado como progresso. Esse valor é recebido pela função
                   callback onProgressUpdate*/
                publishProgress()
                sleep(500)//tempo de 0.5 segundos
            }
            return JSONObject()
        }

        override fun onPostExecute(result: JSONObject?) {

            var data: String? = ""
            var hora: String? = ""
            var ds: String? = ""
            super.onPostExecute(result)

            try {
                data = "${result?.getInt("mday")}/${result?.getInt("mon")}/${result?.getInt("year")}"
                hora = "${result?.getInt("hours")}:${result?.getInt("minutes")}:${result?.getInt("seconds")}"
                ds = result?.getString("weekday")
            } catch (jsone: JSONException) {
                jsone.printStackTrace()
            }
            tv_data.setText("$data\n$hora\n$ds")// Altera o TextView com o texto recuperado
            progressBar.visibility = View.GONE

        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
            values[0]?.apply { pb_carregando.progress }// Se o valor de progresso não for nulo, atualiza a barra de progresso
        }
    }

    //função usada para passar mensagens
    fun messageToast(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
}






























































