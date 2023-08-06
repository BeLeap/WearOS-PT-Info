package codes.beleap.wearos_pt_info.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false, name = "ServiceResult")
data class BusArrivalInfoResponse(
    @field:Element(name = "msgHeader")
    @param:Element(name = "msgHeader")
    val header: Header,
    @field:ElementList(name = "msgBody")
    @param:ElementList(name = "msgBody")
    val body: List<Item>,
)

data class Header(
    @field:Element(name="headerCd")
    @param:Element(name="headerCd")
    val headerCd: Int,
    @field:Element(name="headerMsg")
    @param:Element(name="headerMsg")
    val headerMsg: String,
    @field:Element(name="itemCount")
    @param:Element(name="itemCount")
    val itemCount: Int,
)

@Root(name = "itemList", strict = false)
data class Item(
    @field:Element(name="arrmsg1")
    @param:Element(name="arrmsg1")
    val arrivalMessage: String,
    @field:Element(name="rtNm")
    @param:Element(name="rtNm")
    val routeName: String,
)