package ipca.example.lastestnews

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.insert
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row_article.*
import org.json.JSONObject
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    //all https://newsapi.org/v2/top-headlines?country=pt&apiKey=f1a2b15f14e3469096c1203d1c0350f7
    //business https://newsapi.org/v2/top-headlines?country=pt&category=business&apiKey=f1a2b15f14e3469096c1203d1c0350f7
    //health https://newsapi.org/v2/top-headlines?country=pt&category=health&apiKey=f1a2b15f14e3469096c1203d1c0350f7
    //technology http://newsapi.org/v2/top-headlines?country=pt&category=technology&apiKey=f1a2b15f14e3469096c1203d1c0350f7
    //science https://newsapi.org/v2/top-headlines?country=pt&category=science&apiKey=f1a2b15f14e3469096c1203d1c0350f7
    //sports https://newsapi.org/v2/top-headlines?country=pt&category=sports&apiKey=f1a2b15f14e3469096c1203d1c0350f7
    //entertainment https://newsapi.org/v2/top-headlines?country=pt&category=entertainment&apiKey=f1a2b15f14e3469096c1203d1c0350f7

    var articles: MutableList<Article> = ArrayList<Article>()

    var articlesAdapter = ArticlesAdapter()

    private fun createNews (){

        object : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String {

                var url = URL(BASE_API + PATH + API_KEY)
                var urlContent = url.readText(Charset.defaultCharset())
                Log.d("lastestnews", urlContent)

                return urlContent
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                val jsonResult = JSONObject(result)
                var okResult = jsonResult.getString("status")
                if (okResult.compareTo("ok") == 0) {
                    Toast.makeText(this@MainActivity, "Boas Noticias", Toast.LENGTH_LONG).show()

                    val articleJsonArray = jsonResult.getJSONArray("articles")

                    articles.clear()

                    for (index in 0 until articleJsonArray.length()) {
                        val jsonArticle = articleJsonArray[index] as JSONObject
                        articles.add(Article.parseJson(jsonArticle))
                    }

                    articlesAdapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Erro ao descrregar noticias",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }.execute()

        listViewArticles.adapter = articlesAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_news_type, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.ItemAllNews){
            PATH = "top-headlines?country=pt"
            createNews()
            return true
        }
        else if (item.itemId == R.id.ItemBusiness){
            PATH = "top-headlines?country=pt&category=business"
            createNews()
            return true
        }
        else if (item.itemId == R.id.ItemHealth){
            PATH = "top-headlines?country=pt&category=health"
            createNews()
            return true
        }
        else if (item.itemId == R.id.ItemTechnology){
            PATH = "top-headlines?country=pt&category=technology"
            createNews()
            return true
        }
        else if (item.itemId == R.id.ItemScience){
            PATH = "top-headlines?country=pt&category=science"
            createNews()
            return true
        }
        else if (item.itemId == R.id.ItemSports){
            PATH = "top-headlines?country=pt&category=sports"
            createNews()
            return true
        }
        else if (item.itemId == R.id.ItemEntertainment){
            PATH = "top-headlines?country=pt&category=entertainment"
            createNews()
            return true
        }
        return true
    }

    inner class ArticlesAdapter : BaseAdapter(){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view  = layoutInflater.inflate(R.layout.row_article, parent, false)

            val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)
            val textViewDate  = view.findViewById<TextView>(R.id.textViewDate)
            val imageViewArticle = view.findViewById<ImageView>(R.id.imageView)

            textViewTitle.text = articles[position].title
            textViewDate.text  = articles[position].publishedAt

            object : AsyncTask<Void,Void,Bitmap>(){
                override fun doInBackground(vararg params: Void?): Bitmap? {

                    try {

                        val url = URL(articles[position].urlToImage)
                        val input = url.openStream()
                        val bmp = BitmapFactory.decodeStream(input)
                        return bmp
                    }
                    catch (e: MalformedURLException){
                    return null
                    }
                }

                override fun onPostExecute(result: Bitmap?) {
                    super.onPostExecute(result)
                    result?.let{

                        imageViewArticle.setImageBitmap(it)
                    }

                }

            }.execute()

            view.setOnClickListener {
                val intent = Intent(this@MainActivity, ArticleDetailActivity::class.java)

                intent.putExtra("title", articles[position].title)
                intent.putExtra("url", articles[position].url)

                startActivity(intent)
            }

            return view
        }

        override fun getItem(position: Int): Any {
            return articles[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return articles.size
        }
    }

    companion object{

        var BASE_API = "https://newsapi.org/v2/"
        var PATH     = "top-headlines?country=pt"
        var API_KEY  = "&apiKey=f1a2b15f14e3469096c1203d1c0350f7"
    }
}
