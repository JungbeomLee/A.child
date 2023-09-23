package com.woojun.ai.fragment.main

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.ai.databinding.FragmentSearchBinding
import com.woojun.ai.util.ChildInfoType
import com.woojun.ai.util.ChildrenInfoAdapter
import com.woojun.ai.util.FragmentInteractionListener
import com.woojun.ai.util.SearchAdapter
import com.woojun.ai.util.ViewModel
import java.io.ByteArrayOutputStream

class SearchFragment : Fragment(), FragmentInteractionListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            apiArea.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    apiInputLayout.hint = null
                } else {
                    apiInputLayout.hint = "실종아동 이름을 입력해주세요"
                }
            }

            searchTextList.layoutManager = LinearLayoutManager(requireContext().applicationContext)
            searchTextList.adapter = SearchAdapter(readStringListFromInternalStorage(requireContext(), "search_text"), this@SearchFragment)

            apiArea.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    searchAction(apiArea.text.toString())
                    true
                } else {
                    false
                }
            }

            if (readStringListFromInternalStorage(requireContext(), "search_text")[0] == "") {
                searchTextList.visibility = View.GONE
            } else {
                searchTextList.visibility = View.VISIBLE
            }

        }
    }

    private fun writeStringListToInternalStorage(context: Context, filename: String, stringList: ArrayList<String>) {
        try {
            val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            val fileContent = stringList.joinToString("\n")
            outputStream.write(fileContent.toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readStringListFromInternalStorage(context: Context, filename: String): ArrayList<String> {
        val resultList = ArrayList<String>()

        try {
            val inputStream = context.openFileInput(filename)
            val byteStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                byteStream.write(buffer, 0, bytesRead)
            }

            val fileContent = byteStream.toString("UTF-8")
            inputStream.close()

            resultList.addAll(fileContent.split("\n"))

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "오류 발생 앱을 삭제한 후 다시 설치해주세요", Toast.LENGTH_SHORT).show()
        }

        return resultList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun searchAction(name: String) {
        binding.apply {
            searchTextList.layoutManager = LinearLayoutManager(requireContext().applicationContext)
            searchTextList.adapter = SearchAdapter(readStringListFromInternalStorage(requireContext(), "search_text"), this@SearchFragment)

            textView.visibility = View.GONE
            searchTextList.visibility = View.GONE

            val searchTextList = readStringListFromInternalStorage(requireContext(), "search_text")
            searchTextList.add(name)
            writeStringListToInternalStorage(requireContext(), "search_text", removeEmptyAndDuplicateStrings(searchTextList))

            viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

            viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->
                val data = apiData.filter {
                    it.nm == name
                }

                searchList.layoutManager = LinearLayoutManager(requireContext().applicationContext)
                searchList.adapter = ChildrenInfoAdapter(data.toMutableList(), ChildInfoType.SEARCH)
            }

        }


    }

    override fun removeAction(name: String) {
        binding.apply {
            val searchTexts = readStringListFromInternalStorage(requireContext(), "search_text")
            searchTexts.remove(name)
            writeStringListToInternalStorage(requireContext(), "search_text", searchTexts)

            searchTextList.layoutManager = LinearLayoutManager(requireContext().applicationContext)
            searchTextList.adapter = SearchAdapter(readStringListFromInternalStorage(requireContext(), "search_text"), this@SearchFragment)

            if (readStringListFromInternalStorage(requireContext(), "search_text")[0] == "") {
                searchTextList.visibility = View.GONE
            } else {
                searchTextList.visibility = View.VISIBLE
            }
        }
    }

    private fun removeEmptyAndDuplicateStrings(inputList: List<String>): ArrayList<String> {
        val uniqueNonEmptyStrings = HashSet<String>()
        val result = arrayListOf<String>()

        for (item in inputList) {
            if (item.isNotBlank() && !uniqueNonEmptyStrings.contains(item)) {
                uniqueNonEmptyStrings.add(item)
                result.add(item)
            }
        }

        return result
    }

}