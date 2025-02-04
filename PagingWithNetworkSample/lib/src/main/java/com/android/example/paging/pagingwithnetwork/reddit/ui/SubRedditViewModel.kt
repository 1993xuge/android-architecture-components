/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.paging.pagingwithnetwork.reddit.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.android.example.paging.pagingwithnetwork.reddit.repository.RedditPostRepository

class SubRedditViewModel(private val repository: RedditPostRepository) : ViewModel() {

    companion object {
        private val TAG = SubRedditViewModel::class.java.simpleName
    }

    /**
     * 存储的是 搜索的值
     */
    private val subredditName = MutableLiveData<String>()

    /**
     * Listing<RedditPost>
     */
    private val repoResult = map(subredditName) {

        Log.d(TAG, "repoResult  subredditName = $it")
        repository.postsOfSubreddit(it, 30)
    }

    val posts = switchMap(repoResult, {
        it.pagedList
    })!!

    val networkState = switchMap(repoResult, { it.networkState })!!

    val refreshState = switchMap(repoResult, { it.refreshState })!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    /**
     * 更新 搜索的值。然后触发 数据请求
     *
     * 如果数据未变化，则不请求
     */
    fun showSubreddit(subreddit: String): Boolean {
        if (subredditName.value == subreddit) {
            return false
        }
        subredditName.value = subreddit
        return true
    }

    /**
     * 重试
     */
    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    fun currentSubreddit(): String? = subredditName.value
}
