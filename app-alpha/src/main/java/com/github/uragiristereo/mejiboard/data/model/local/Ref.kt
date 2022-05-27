package com.github.uragiristereo.mejiboard.data.model.local

data class Ref<T>(var value: T)

fun <T> refOf(value: T): Ref<T> {
    return Ref(value)
}