package kr.or.payot.poin.Activities.Scan

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * Created by yongheekim on 2018. 2. 19..
 */
class PointOverlayView : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val paint: Paint

    private var points: Array<out PointF>? = null


    init {
        paint = Paint()
        paint.color = Color.YELLOW
        paint.style = Paint.Style.FILL
    }

    fun setPoints(points: Array<out PointF>?) {
        this.points = points
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (points == null) {
            canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY)
        } else {
            for (point in points!!) {
                canvas?.drawCircle(point.x, point.y, 10F, paint)
            }
        }
    }
}