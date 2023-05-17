package com.github.ihsg.patternlocker

import android.R.attr.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Message
import android.util.AttributeSet
import android.view.View
import kotlin.math.*


class WaterWaveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr)
{
    private var viewWidth = 0
    private var viewHeight = 0

    // 画笔和颜色
    private var circlePaint: Paint
    private var wavePaint: Paint
    private var mMatrix =Matrix()




    //private val circleColor = Color
    private val waveColor = Color.parseColor("#99dbb788")

    private val circleStrokeWidth = 2f
    private val waveStrokeWidth = 1f

    private val rectF = RectF()
    private val path = Path()


    public var bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img_no_avatar)
    //private var resizedBitmap = Bitmap.createScaledBitmap(bitmap,5,5,false)

    //基本参数
    private val amplitude = 10f         // 振幅
    private var waterLevel = 0f         // 扇形的坐标
    private var baseLevel = 0f          // 扇形的高度
    private val radius = 118f           // 半径
    private var angle = 0f              // 扇形的开始绘制角度
    private var startX = 0f             // sin函数的x
    private var startY = 0f             // sin函数的y
    private var little = 0              // 偏移量
    private var scale=0.32f              //bitmap缩放
    private var mWidth=Math.min(getMeasuredWidth(), getMeasuredHeight())
    private val range = ArrayList<Float>()

    // 进度控制
    private var isStarted = 0
    private var progress = 0f           // 进度 0，0.2，0.4，0.6，0.8，1
    private val messageHandler:android.os.Handler
    private val message = Message()
    private val flow = 1
    private val rise = 2
    private val fall = 3
    private val stop = 4

    init
    {
        circlePaint = Paint()
        val bSize: Int = Math.min(bitmap.getWidth(), bitmap.getHeight())
        //scale = mWidth * 1.0f / bSize
        mMatrix.setScale(scale, scale);
        var bitmapShader=BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
        // 设置变换矩阵
        bitmapShader.setLocalMatrix((mMatrix));
        // 设置shader
        circlePaint.setShader(bitmapShader);


        circlePaint.let {
            //it.color = circleColor
            it.strokeWidth = circleStrokeWidth
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
            //it.setShader(BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP))
            it.isFilterBitmap=true
        }

        wavePaint = Paint()
        wavePaint.let {
            it.color = waveColor
            it.strokeWidth = waveStrokeWidth
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
        }


        //handler
        messageHandler = @SuppressLint("HandlerLeak")
        object : android.os.Handler() {
            private var next = progress + 0.2f
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what)
                {
                    flow -> {
                        invalidate()
                        sendEmptyMessageDelayed(flow,16L)
                    }
                    rise -> {
                        invalidate()
                        if (progress < next)
                        {
                            progress += 0.005f
                            if (progress > 1f){
                                progress = 1f
                            }
                            //removeMessages(flow)
                            //removeMessages(fall)
                            sendEmptyMessageDelayed(rise, 16L)
                        }
                        else
                        {
                            next += 0.2f
                            sendEmptyMessageDelayed(flow, 16L)
                        }
                    }
                    fall -> {
                        invalidate()
                        if (progress > 0f)
                        {
                            progress -= 0.005f
                            removeMessages(flow)
                            removeMessages(rise)
                            sendEmptyMessageDelayed(fall, 16L)
                        }
                        else{
                            progress = 0f
                            next = 0.2f
                            sendEmptyMessageDelayed(flow, 16L)
                        }
                    }
                    stop -> {

                        removeMessages(flow)
                    }
                }
                //invalidate()
                //
                // sendEmptyMessageDelayed(flow,16L)
            }
        }

        for (i in 0..(2*radius).toInt())
        {

            range.add(sqrt(2*radius*i - i*i))
        }

    }

    public fun startWave()
    {
        //isStarted = 1
        //progress = 0f
        message.what = flow
        this.messageHandler.sendMessage(message)
        //this.messageHandler.sendEmptyMessageDelayed(stop, 1000L)
    }

    public fun startRise()
    {

        this.messageHandler.sendEmptyMessage(rise)
    }

    public fun startFall()
    {
        this.messageHandler.sendEmptyMessage(fall)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {    setMeasuredDimension(mWidth, mWidth);
        val a = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(a, a)
    }

    private lateinit var can:Canvas
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = this.width.toFloat()
        val height = this.height.toFloat()
        val center_x = width / 2
        val center_y = height / 2
        val left = center_x - radius
        val right = center_x + radius
        val top = center_y - radius
        val bottom = center_y + radius



        canvas?.save()
        //切割出圆形区域
        path.addCircle(center_x, center_y, radius, Path.Direction.CCW)
        //canvas?.clipPath(path)

        //画圆
        //canvas?.drawPath(path, circlePaint)
        //canvas?.drawBitmap(bitmap,center_x,center_y,circlePaint)
        canvas?.drawCircle(center_x,center_y,radius,circlePaint)

        //根据进度，计算扇形的高度
        baseLevel = 2 * radius * progress - 2* amplitude
        //计算扇形在view中的坐标
        waterLevel = bottom - baseLevel
        //计算扇形的开始角度
        val tempsin = (radius - baseLevel) / radius
        angle = (asin(tempsin) * 180f / PI).toFloat()
        //画扇形

        rectF.let {
            it.left = center_x - radius
            it.top = center_y - radius
            it.right = center_x + radius
            it.bottom = center_y + radius

        }
        canvas?.drawArc(rectF, angle, 180f - 2*angle, false, wavePaint)

        /*//计算开始的画线位置
        //扇形高度+振幅 为 正弦曲线的x轴高度
        //val temp = baseLevel + amplitude
        //根据该高度计算弦长
        //val chord = sqrt(2*radius*temp - temp.pow(2))
        //画线的开始和结束位置为 center_x -+ chord
        //startX = center_x - chord*/
        //每次增加偏移量
        little++
        if (little == 66666)
        {
            little = 0
        }

        startX = left
        val r2 = radius * radius
        while (startX < right)
        {
            startY = waterLevel - amplitude + (amplitude * Math.sin(Math.PI * startX *0.002f+ 0.05f*little).toFloat())
            var endY = waterLevel
            var i = startX - left
            val isStartOut = (startX - center_x).pow(2) + (startY - center_y).pow(2) > r2
            val isEndOut = (startX - center_x).pow(2) + (waterLevel - center_y).pow(2) > r2
            if( isStartOut && !isEndOut)
            {
                startY = center_y - range[i.toInt()]
                canvas?.drawLine(startX,startY,startX,endY,wavePaint)
            }
            else if (!isStartOut && isEndOut)
            {
                endY = center_y + range[i.toInt()]
                canvas?.drawLine(startX,startY,startX,endY,wavePaint)
            }
            else if (!isStartOut && !isEndOut)
            {
                canvas?.drawLine(startX,startY,startX,endY,wavePaint)
            }

            startX = startX + 1f
        }
        canvas?.restore()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

}