package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.FragmentArticleDetailBinding

class ArticleDetailFragment : Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    private var articleTitle: String? = null
    private var articleIcon: String? = null
    private var articleContent: String? = null

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_ICON = "icon"
        private const val ARG_CONTENT = "content"

        fun newInstance(title: String, icon: String, content: String): ArticleDetailFragment {
            val fragment = ArticleDetailFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_ICON, icon)
            args.putString(ARG_CONTENT, content)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            articleTitle = it.getString(ARG_TITLE)
            articleIcon = it.getString(ARG_ICON)
            articleContent = it.getString(ARG_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvArticleIcon.text = articleIcon
        binding.tvArticleTitle.text = articleTitle
        binding.tvArticleContent.text = articleContent

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
