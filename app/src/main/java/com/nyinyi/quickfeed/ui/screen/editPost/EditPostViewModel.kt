package com.nyinyi.quickfeed.ui.screen.editPost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.domain.usecase.post.GetPostDetailUseCase
import com.nyinyi.domain.usecase.post.UpdatePostUseCase
import com.nyinyi.domain_model.Post
import com.nyinyi.quickfeed.provider.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val getPostDetailUseCase: GetPostDetailUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _state = MutableStateFlow<EditPostUiState>(
        EditPostUiState()
    )
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<EditPostErrorViewEvent>()
    val event = _event.asSharedFlow()


    fun getPostDetail(postId: String) {
        viewModelScope.launch(dispatcherProvider.io()) {
            _state.value = _state.value.copy(isLoading = true, postId = postId)
            getPostDetailUseCase(postId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        post = it,
                        isLoading = false
                    )
                }.onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = it.message
                    )
                    _event.emit(EditPostErrorViewEvent.ErrorOnLoading(it.message ?: ""))
                }
        }

    }

    fun updatePost(
        image: InputStream?,
        content: String
    ) {
        viewModelScope.launch(dispatcherProvider.io()) {
            Timber.d("updatePost: $content")
            _state.value = _state.value.copy(isLoading = true)
            updatePostUseCase(
                imageInputStream = image,
                text = content,
                postId = _state.value.postId
            ).onSuccess {
                Timber.d("updatePost: Success")
                _state.value = _state.value.copy(
                    isLoading = false
                )
                _event.emit(EditPostErrorViewEvent.Success("Post updated successfully"))
            }.onFailure {
                Timber.d("updatePost: Failed")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = it.message
                )
                _event.emit(EditPostErrorViewEvent.ErrorOnUpdate(it.message ?: ""))
            }
        }

    }

}

data class EditPostUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val post: Post? = null,
    val postId: String = ""
)

sealed class EditPostErrorViewEvent {
    data class Success(val message: String) : EditPostErrorViewEvent()
    data class ErrorOnUpdate(val message: String) : EditPostErrorViewEvent()
    data class ErrorOnLoading(val message: String) : EditPostErrorViewEvent()
}