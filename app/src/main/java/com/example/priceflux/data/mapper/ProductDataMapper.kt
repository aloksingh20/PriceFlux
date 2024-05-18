package com.example.priceflux.data.mapper

import com.example.priceflux.data.local.product.ProductEntity
import com.example.priceflux.data.remote.RemoteDto


fun RemoteDto.toProductEntity(): ProductEntity {
    return ProductEntity(
        productName = productName,
        productPrice = productPrice,
        productImage = productImage,
        productUrl =   productUrl,
        prodDescription = prodDescription,
        productRating = productRating,
        previousPrice = productPrice
    )
}

fun ProductEntity.toRemoteDto():RemoteDto{
    return RemoteDto(
        productName = productName,
        productPrice = productPrice,
        productImage = productImage,
        productUrl =   productUrl,
        prodDescription = prodDescription,
        productRating = productRating
    )
}