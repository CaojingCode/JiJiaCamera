# JiJiaCamera
录视频库
[![](https://jitpack.io/v/CaojingCode/JiJiaCamera.svg)](https://jitpack.io/#CaojingCode/JiJiaCamera)
 
 跳转到视频拍摄页面：
 startRecordVideo()
 
 跳转到视频页面的时候可以同时定位，定位成功后可以通过发送广播：VideoAddressAction(常量) 来将位置信息
 VideoAddress(常量)通过intent传给拍摄页面。
 
 
 跳转到选择带看视频页面：
 selectLookVideo()
 跳转到选择视频页面可以重写onActivityResult来接收到选中的视频对象，目前只支持选中一个视频，后期可以扩展，
 通过接收"videoBean" 来接收 VideoBean 对象。