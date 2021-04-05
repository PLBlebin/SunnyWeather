package com.non.sunnyweather.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.non.sunnyweather.MainActivity
import com.non.sunnyweather.R
import com.non.sunnyweather.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*

class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        placeRecyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        placeRecyclerView.adapter = adapter

        initPlace()

        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                if (viewModel.isPlaceRecord()) {
                    placeRecyclerView.visibility = View.VISIBLE
                    cleanBtn.visibility = View.VISIBLE
                    bgImageView.visibility = View.GONE
                    viewModel.placeList.clear()
                    viewModel.placeList.add(viewModel.getSavedPlaceRecord())
                    adapter.notifyDataSetChanged()
                }
            }
        }

        viewModel.placeLiveData.observe(this, Observer{ result ->
            val places = result.getOrNull()
            if (places != null) {
                placeRecyclerView.visibility = View.VISIBLE
                cleanBtn.visibility = View.GONE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        cleanBtn.setOnClickListener {
            viewModel.cleanPlaceRecord()
            placeRecyclerView.visibility = View.GONE
            cleanBtn.visibility = View.GONE
            bgImageView.visibility = View.VISIBLE
        }
    }

    private fun initPlace() {
        if (viewModel.isPlaceRecord()){
            placeRecyclerView.visibility = View.VISIBLE
            cleanBtn.visibility = View.VISIBLE
            bgImageView.visibility = View.GONE
            viewModel.placeList.clear()
            viewModel.placeList.add(viewModel.getSavedPlaceRecord())
            adapter.notifyDataSetChanged()
        }
    }
}