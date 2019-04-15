# Android平台图像压缩方案

**关于作者**

>郭孝星，程序员，吉他手，主要从事Android平台基础架构方面的工作，欢迎交流技术方面的问题，可以去我的[Github](https://github.com/guoxiaoxing)提issue或者发邮件至guoxiaoxingse@163.com与我交流。

**文章目录**

在日常开发中，视频的压缩与上传也是常见的业务需求，因为视频有着比图像更强的表现力，也就更受产品需求的青睐。但是Android平台视频相关的开发，大概是整个Android生态最为
分裂，兼容性问题最为突出的一部分。不同厂商对摄像采集以及编解码的实现有很大差异，这给我们在适配上带来了很大的麻烦。今天我们就这些问题讨论视频压缩方案的实现。

首先让我们先了解一下和视频压缩相关的基础理论。

## 一 视频压缩基础理论

编解码器

- 编码器（Encoder）：压缩信号的设备或程序；
- 解码器（Decoder）：解压缩信号的设备或程序；
- 编解码器(Codec)：编解码器对。

压缩分类

- 无损压缩（Lossless）：压缩前、解压缩后图像完全一致X=X'，压缩比低(2:1~3:1)。典型格式例如：Winzip，JPEG-LS。
- 有损压缩（Lossy）：压缩前解压缩后图像不一致X≠X'，压缩比高(10:1~20:1)，利用人的视觉系统的特性。典型格式例如：MPEG-2，H.264/AVC，AVS。

了解了这些基础的概念，我们来思考两个问题。

首先来说第一个问题，为什么要压缩视频？🤔

- 未经压缩的视频带有大量信息，可能会泄漏用户隐私，这和图像是一样的。
- 未经压缩的视频会占用大量存储空间，现在的手机都1080P甚至4k，如果不做压缩，视频读写的时候也可能会报OOM。
- 未经压缩的视频会在传输时会占用大量带宽，一个60s的视频可达到几十M的大小，直接上传几十M的文件会给用户带来很大的流量消耗，接口本身也很容易超时。

再来看看第二个问题，视频压缩到底压缩了什么？🤔

- 空间冗余：图像相邻像素之间有较强的相关性
- 时间冗余：视频序列的相邻图像之间内容相似
- 编码冗余：不同像素值出现的概率不同
- 视觉冗余：人的视觉系统对某些细节不敏感
- 知识冗余：规律性的结构可由先验知识和背景知识得到

## 二 视频压缩方案实现

Android平台对于视频编解码而言，常见的方案选择有两种：

- MediaCodec：Android于API 16之后推出的用于音视频编解码的一套底层API，可以利用硬件加速进行编解码，它的优点是系统自带，不用引用第三方库，
- FFMpeg + x264/openh264：FFMpeg大家都很熟悉，这里一般用FFMpeg做视频帧的预处理，然后再使用x264/openh264作为视频的编码器，

关于x264

>[x264](https://www.videolan.org/developers/x264.html)是一个采用GPL授权的视频编码自由软体。x264的主要功能在于进行H.264/MPEG-4 AVC的视频编码，而不是作为解码器（decoder）之用。它基本支持h264的
所有特性。

关于openh264

>[openh264](http://www.openh264.org/)：是由思科开源的另外一个h264编码器，但对比起x264，openh264在h264高级特性的支持相对较差。

软解最大的问题就是编解码效率的问题，因为Android手机自身的限制，它只能使用CPU对视频进行逐帧图像处理，效率非常的低，可以说在产品使用上是难以让人接受的。本篇文章主要
讨论Android视频压缩硬解码的实现，如果对软解码感兴趣，可以参考下这个项目[small-video-record](https://github.com/mabeijianxi/small-video-record)

它的压缩原理本质上来说就是利用FFmpeg的命令行就行视频的重新编解码处理，改变视频的大小，帧率从而达到压缩的目的，它的编解码命令是：

>ffmpeg -threads 16 -i /storage/emulated/0/DCIM/Camera/VID_20171120_110201.mp4 -c:v libx264  -crf 28 -preset ultrafast  -c:a libfdk_aac  -vbr 4    /storage/emulated/0/DCIM/souche/1511155800958/1511155800958.mp4

我们接着进入今天的正题：基于MediaCodec的Android平台视频压缩方案的实现。

>[MediaCodec](https://developer.android.com/reference/android/media/MediaCodec.html)是一套偏底层的音视频编解码器，可以利用硬件加速进行硬解码。

<img src="https://github.com/guoxiaoxing/phoenix/raw/master/art/codec/MediaCodec.png"/>

MediaCodec等一系列的类主要用来编解码音视频，整个家族成员主要包括以下几个类：

- MediaCodec：用来访问底层媒体编解码器，即编码器/解码器的部件。
- MediaExtractor
- MediaSync
- MediaMuxer
- MediaCrypto
- MediaDrm
- Image
- Surface
- AudioTrack

