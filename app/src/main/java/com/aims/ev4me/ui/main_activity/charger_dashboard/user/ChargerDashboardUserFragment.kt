package com.aims.ev4me.ui.main_activity.charger_dashboard.user

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aims.ev4me.BuildConfig
import com.aims.ev4me.databinding.FragmentChargerDashboardUserBinding
import com.aims.ev4me.getBitmapFromURL
import java.util.concurrent.Executors

class ChargerDashboardUserFragment : Fragment() {

    private var _binding: FragmentChargerDashboardUserBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chargerDashboardUserViewModel =
            ViewModelProvider(this).get(ChargerDashboardUserViewModel::class.java)

        _binding = FragmentChargerDashboardUserBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val googleMapImageView = binding.googleMapStaticImage

        val executor = Executors.newSingleThreadExecutor()

        val handler = Handler(Looper.getMainLooper())

        var image: Bitmap? = null
        val latLongStr = "34.4115988,-119.8460491"

        executor.execute {
            //val imageURL = "https://pyxis.nymag.com/v1/imgs/1b1/2b6/4a4c800d5af56e0cd00edc9a86e06ae345-11-puppies.2x.rhorizontal.w700.jpg"
            val imageURL = "https://maps.googleapis.com/maps/api/staticmap?&size=745x246&markers=color:red|$latLongStr&key=${BuildConfig.MAPS_API_KEY}"
            //val imageURL = "https://maps.googleapis.com/maps/api/staticmap?center=$latLongStr&size=1490x492&maptype=roadmap&markers=color:red|$latLongStr&key=${BuildConfig.MAPS_API_KEY}&signature=${BuildConfig.STATIC_MAPS_API_KEY}"
            /*
            val imageURL = "https://maps.googleapis.com/maps/api/staticmap?center=$latLongStr&size=1490x492&maptype=roadmap\n" +
                    "&markers=color:red|$latLongStr\n" +
                    "&key=${BuildConfig.MAPS_API_KEY}&signature=${BuildConfig.STATIC_MAPS_API_KEY}"
*/
            image = getBitmapFromURL(imageURL)
            handler.post{

                googleMapImageView.setImageBitmap(image)
            }
            /*
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)

                // Only for making changes in UI
                handler.post {
                    imageView.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
             */
        }

        //Set up google maps intent
        val googleMapsDirectionsIntent = Uri.parse("google.navigation:q=$latLongStr")
        val googleMapsIntent = Intent(Intent.ACTION_VIEW, googleMapsDirectionsIntent)
        googleMapsIntent.`package` = "com.google.android.apps.maps"
        googleMapImageView.setOnClickListener {
            activity?.let { it1 -> startActivity(googleMapsIntent) }
        }




//        val textView: TextView = binding.textDashboard
//        chargerDashboardUserViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}