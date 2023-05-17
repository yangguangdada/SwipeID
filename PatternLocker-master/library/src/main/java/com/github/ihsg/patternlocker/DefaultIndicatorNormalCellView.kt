package com.github.ihsg.patternlocker

import android.graphics.Canvas
import android.graphics.Paint

/**
 * Created by hsg on 22/02/2018.
 */

open class DefaultIndicatorNormalCellView(val styleDecorator: DefaultStyleDecorator) : INormalCellView {

    private val paint: Paint by lazy {
        DefaultConfig.createPaint()
    }

    init {
        this.paint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas, cellBean: CellBean) {
        val saveCount = canvas.save()

        //outer circle
        this.paint.color = this.styleDecorator.normalColor
        canvas.drawCircle(cellBean.centerX, cellBean.centerY, cellBean.radius, this.paint)
        /*this.paint.color = this.styleDecorator.normalColor
        canvas.drawPoint(cellBean.centerX, cellBean.centerY, this.paint)*/

        //inner circle
        this.paint.color = this.styleDecorator.fillColor
        canvas.drawCircle(cellBean.centerX, cellBean.centerY, cellBean.radius - this.styleDecorator.lineWidth, this.paint)

        canvas.restoreToCount(saveCount)
    }
}