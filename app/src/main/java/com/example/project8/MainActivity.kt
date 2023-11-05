package com.example.project8

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.project8.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val BASE_URL = "https://www.omdbapi.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val searchButton = findViewById<Button>(R.id.bSearchMovie)
        val movieSearchText = findViewById<EditText>(R.id.editTextMovieSearchName)

        val movieContainer = binding.movieInfoContainer

        // send email when user clicks feedback button
        binding.bFeedback.setOnClickListener {
            sendFeedbackEmail()
        }

        // when we hit search button
        searchButton.setOnClickListener {
            val movieSearchTitle = movieSearchText.text?.toString()
            if (movieSearchTitle != null)
            {
                val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
                val apiInterface = retrofit.create(OMDbApi::class.java)
                // send request to OMDB based on the user-inputted title
                apiInterface.getMovie(movieSearchTitle).enqueue(object :
                    Callback<MovieSearchResult> {
                        override fun onResponse(call: Call<MovieSearchResult>, response: Response<MovieSearchResult>) {
                            Log.i("GetMovie", "onResponse $response")
                            val body = response.body()
                            if (body == null) {
                                Log.w("GetMovie", "Did not receive valid response body from OMDB API... exiting")
                                return
                            }
                            movieContainer.visibility = VISIBLE
                            SetupMovieContainer(response.body()!!, binding)
                            Log.i("GetMovie", "response body:\n$body")
                        }
                        override fun onFailure(call: Call<MovieSearchResult>, t: Throwable) {
                            Log.i("GetMovie", "onFailure $t")
                        }
                    }
                )
            }
        }

    }

    fun SetupMovieContainer(searchResult: MovieSearchResult, movieContainer: ActivityMainBinding)
    {
        // populate the movie container with results from our search

        val movieTitleText = movieContainer.movieTitle
        movieTitleText.setText(searchResult.title)

        val movieYearText = movieContainer.releaseYear
        movieYearText.setText(searchResult.year.toString())

        val movieRatedText = movieContainer.ratingText
        movieRatedText.setText(searchResult.rated)

        val movieRuntimeText = movieContainer.runningTime
        movieRuntimeText.setText(searchResult.runtime)

        val movieGenreText = movieContainer.genre
        movieGenreText.setText(searchResult.genre)

        // load image in from glide into our imageview
        val movieImageView = movieContainer.movieImage
        Glide.with(applicationContext).load(searchResult.posterURL)
            .apply(
                RequestOptions().transform(
                    CenterCrop(), RoundedCorners(20)
                )
            )
            .into(movieImageView)

        val movieImdbRating = movieContainer.imdbRating
        movieImdbRating.setText(searchResult.imdbRating)

        val movieImdbLink = movieContainer.imdbPageLink
        val imdbLinkStr: String = getImdbURL(searchResult.imdbID)
        movieImdbLink.setText(imdbLinkStr)

        // launch intent when user clicks share button
        movieContainer.bShare.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, imdbLinkStr)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    fun getImdbURL(imdbID: String) : String
    {
        // imdb link to a specific movie id
        return "https://www.imdb.com/title/$imdbID"
    }

    fun sendFeedbackEmail()
    {
        // send email intent with dev email and feedback subject
        val emailRecipient = "grayclar@iu.edu"
        val emailSubject = "Feedback"
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.setData(Uri.parse("mailto:"))
        emailIntent.setType("text/plain")
        emailIntent.putExtra(Intent.EXTRA_EMAIL  , emailRecipient);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT   , "Feedback Message Body Here");
        startActivity(emailIntent)
        finish()
    }

}