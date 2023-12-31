package com.kinopio.eatgo

object User {
    private var userId : Int? = null
    private var userName : String? = null
    private var userSocialId : String? = null
    private var role : Int? = null
    private var socialToken : String? = null
    private var jwt : String? = null
    private var loginType : Int? = null
    private var positionX : Double? = null
    private var positionY : Double? = null
    private var fireBaseToken : String? = null

    fun setUserId(userId:Int) {
        this.userId = userId;
    }

    fun getUserId() : Int?{
        return this.userId
    }

    fun setUserName(userName:String) {
        this.userName = userName
    }

    fun getUserName() : String? {
        return this.userName
    }

    fun setRole(role:Int) {
        this.role = role
    }

    fun getRole() : Int? {
        return this.role
    }

    fun setSocialToken(token:String) {
        this.socialToken = token
    }

    fun getSocialToken() :String? {
        return this.socialToken
    }

    fun setLoginType(loginType:Int) {
        this.loginType = loginType
    }

    fun getLoginType() : Int?{
        return this.loginType
    }

    fun setJwt(jwt:String) {
        this.jwt = jwt
    }

    fun getJwt(): String? {
        return this.jwt
    }

    fun setUserSocialId(userSocialId : String) {
        this.userSocialId = userSocialId
    }

    fun getUserSocialId() :String?{
        return this.userSocialId
    }

    fun setPositionX(positionX : Double) {
        this.positionX = positionX
    }

    fun getPositionX() : Double? {
        return this.positionX
    }

    fun setPositionY(positionY : Double) {
        this.positionY = positionY
    }

    fun getPositionY() : Double? {
        return this.positionY
    }

    fun setFireBaseToken(fireBaseToken : String) {
        this.fireBaseToken = fireBaseToken
    }

    fun getFireBaseToken() : String? {
        return this.fireBaseToken
    }


}