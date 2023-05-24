package com.example.xbiztask2.model

import com.google.gson.annotations.SerializedName

data class PinCodeResponse(
    val pinCodeResponse: List<PinCodeResponseItem>,
)

data class PinCodeResponseItem(

    @field:SerializedName("Status")
    var status: String,

    @field:SerializedName("Message")
    var message: String,

    @field:SerializedName("PostOffice")
    val postOffice: List<PostOfficeItem>,
    var throwable: Throwable? = null

    )

data class PostOfficeItem(

    @field:SerializedName("Circle")
    val circle: String,

    @field:SerializedName("Description")
    val description: Any,

    @field:SerializedName("BranchType")
    val branchType: String,

    @field:SerializedName("State")
    val state: String,

    @field:SerializedName("DeliveryStatus")
    val deliveryStatus: String,

    @field:SerializedName("Region")
    val region: String,

    @field:SerializedName("Block")
    val block: String,

    @field:SerializedName("Country")
    val country: String,

    @field:SerializedName("Division")
    val division: String,

    @field:SerializedName("District")
    val district: String,

    @field:SerializedName("Pincode")
    val pincode: String,

    @field:SerializedName("Name")
    val name: String,
)
