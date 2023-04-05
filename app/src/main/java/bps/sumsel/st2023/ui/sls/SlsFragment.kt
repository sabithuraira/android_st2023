package bps.sumsel.st2023.ui.sls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bps.sumsel.st2023.MainViewModel
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentNotificationsBinding
import bps.sumsel.st2023.databinding.FragmentSlsBinding
import bps.sumsel.st2023.room.entity.SlsEntity
import bps.sumsel.st2023.ui.notifications.NotificationsViewModel

class SlsFragment : Fragment() {
    private var _binding: FragmentSlsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvSls.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvSls.addItemDecoration(itemDecoration)

        viewModel.getSls().observe(viewLifecycleOwner){
            val slsAdapter = SlsAdapter(ArrayList(it))
            slsAdapter.setOnClickCallBack(object: SlsAdapter.onClickCallBack{
                override fun onItemClicked(data: SlsEntity) {
                    editData(view, data)
                }
            })
            binding.rvSls.adapter = slsAdapter
        }
    }

    private fun editData(view: View, data: SlsEntity){
//        view.findNavController().navigate(
//            TodosFragmentDirections.actionTodosFragmentToFormFragment(data)
//        )
    }
}