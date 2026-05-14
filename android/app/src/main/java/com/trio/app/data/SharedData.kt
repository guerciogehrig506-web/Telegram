package com.trio.app.data

import androidx.compose.ui.graphics.Color
import com.trio.app.data.model.Contact
import com.trio.app.data.model.OnlineStatus
import com.trio.app.data.model.User
import com.trio.app.ui.theme.TG_AvatarGradients

object SharedData {
    val contacts: List<Contact> = listOf(
        Contact(User("1", "张伟", "zhangwei@trio.app", "USER", "技术部", "", "", true, "", "", ""), OnlineStatus.ONLINE, TG_AvatarGradients[0]),
        Contact(User("2", "李娜", "lina@trio.app", "ADMIN", "产品部", "", "", true, "", "", ""), OnlineStatus.ONLINE, TG_AvatarGradients[1]),
        Contact(User("3", "王芳", "wangfang@trio.app", "USER", "设计部", "", "", true, "", "", ""), OnlineStatus.AWAY, TG_AvatarGradients[2]),
        Contact(User("4", "赵强", "zhaoqiang@trio.app", "USER", "技术部", "", "", true, "", "", ""), OnlineStatus.OFFLINE, TG_AvatarGradients[3]),
        Contact(User("5", "刘洋", "liuyang@trio.app", "USER", "产品部", "", "", true, "", "", ""), OnlineStatus.ONLINE, TG_AvatarGradients[4]),
        Contact(User("6", "陈静", "chenjing@trio.app", "USER", "设计部", "", "", true, "", "", ""), OnlineStatus.OFFLINE, TG_AvatarGradients[5]),
        Contact(User("7", "孙涛", "suntao@trio.app", "USER", "运营部", "", "", true, "", "", ""), OnlineStatus.AWAY, TG_AvatarGradients[6]),
        Contact(User("8", "周明", "zhouming@trio.app", "USER", "技术部", "", "", false, "", "", ""), OnlineStatus.OFFLINE, TG_AvatarGradients[0]),
        Contact(User("9", "吴婷", "wuting@trio.app", "ADMIN", "运营部", "", "", true, "", "", ""), OnlineStatus.ONLINE, TG_AvatarGradients[1]),
        Contact(User("10", "郑宇", "zhengyu@trio.app", "USER", "产品部", "", "", true, "", "", ""), OnlineStatus.ONLINE, TG_AvatarGradients[3])
    )

    private val contactMap: Map<String, Contact> = contacts.associateBy { it.user.id }

    fun getContact(userId: String): Contact = contactMap[userId] ?: contacts.first()

    fun getContactByName(name: String): Contact? = contacts.firstOrNull { it.user.username == name }
}