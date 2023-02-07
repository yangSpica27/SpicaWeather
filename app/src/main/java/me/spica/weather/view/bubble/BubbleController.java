package me.spica.weather.view.bubble;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import me.spica.weather.model.Bubble;
import me.spica.weather.tools.DegreeUtil;
import me.spica.weather.tools.GeometryUtil;

/**
 * @ClassName BubbleController
 * @Description 气泡控制器
 * @Author Spica2 7
 * @Date 2023/2/7 9:40
 */
public class BubbleController {

  private String TAG = "BubbleController";

  private Random rd;

  /*** path系数 */
  private float lineSmoothness;
  /*** 气泡的最小半径 */
  private float minBubbleRadius;
  /*** 气泡的最大半径 */
  private float maxBubbleRadius;
  /*** 速率百分比,当气泡与发射器之间有粘合贝塞尔曲线时,运动速度减慢 */
  private float speedRadio;
  /*** 半径变化率 */
  private float radiusRadio;
  /*** 气泡的最小速度 */
  private float minBubbleSpeedY;
  /*** 气泡的最大速度 */
  private float maxBubbleSpeedY;

  /*** 气泡最多有几个 */
  private int maxBubbleCount;
  /*** 气泡颜色 */
  private int color;

  /*** 圆环中心坐标 */
  private int circularCenterX, circularCenterY;
  /*** 圆环半径 */
  private float circularRadius;
  /*** 发射器的中心x坐标 */
  private float emitterCenterX;
  /*** 发射器的中心y坐标 */
  private double emitterCenterY;
  /*** 发射器半径 */
  private float emitterRadius;

  /*** 气泡集合 */
  public List<Bubble> mBubbleList = new ArrayList<>();
  /*** 气泡关于路径的map */
  private Map<Bubble, Path> mBubblePathMap = new HashMap<>();
  //气泡与圆环黏和的点数组
  private PointF[] bubblePoints = new PointF[7];
  /*** 和固定控制点有关的常量 */
  private static final float C = 0.551915024494f;

  public BubbleController(BubbleControllerBuilder builder) {
    if (builder.rd == null) throw new NullPointerException("builder.rd == null");
    if (builder.lineSmoothness <= 0) throw new IllegalArgumentException("lineSmoothness <= 0");
    if (builder.maxBubbleRadius <= builder.minBubbleRadius)
      throw new IllegalArgumentException("builder.maxBubbleRadius <= builder.minBubbleRadius");
    if (builder.maxBubbleSpeedY <= builder.minBubbleSpeedY)
      throw new IllegalArgumentException("builder.maxBubbleSpeedY <= builder.minBubbleSpeedY");
    this.rd = builder.rd;
    this.lineSmoothness = builder.lineSmoothness;
    this.minBubbleRadius = builder.minBubbleRadius;
    this.maxBubbleRadius = builder.maxBubbleRadius;
    this.speedRadio = builder.speedRadio;
    this.radiusRadio = builder.radiusRadio;
    this.minBubbleSpeedY = builder.minBubbleSpeedY;
    this.maxBubbleSpeedY = builder.maxBubbleSpeedY;
    this.maxBubbleCount = builder.maxBubbleCount;
    this.color = builder.color;
    this.circularCenterX = builder.circularCenterX;
    this.circularCenterY = builder.circularCenterY;
    this.circularRadius = builder.circularRadius;
    this.emitterCenterX = builder.emitterCenterX;
    this.emitterCenterY = builder.emitterCenterY;
    this.emitterRadius = builder.emitterRadius;
    for (int i = 0; i < bubblePoints.length; i++) {
      bubblePoints[i] = new PointF();
    }
  }

  static class BubbleControllerBuilder extends Builder<BubbleControllerBuilder> {

    private Random rd;

    /*** 气泡的最小半径 */
    private float minBubbleRadius;
    /*** 气泡的最大半径 */
    private float maxBubbleRadius;
    /*** 速率百分比,当气泡与发射器之间有粘合贝塞尔曲线时,运动速度减慢 */
    private float speedRadio;
    /*** 半径变化率 */
    private float radiusRadio;
    /*** 气泡的最小速度 */
    private float minBubbleSpeedY;
    /*** 气泡的最大速度 */
    private float maxBubbleSpeedY;
    /*** 气泡最多有几个 */
    private int maxBubbleCount;
    /*** 圆环中心坐标 */
    private int circularCenterX, circularCenterY;
    /*** 圆环半径 */
    private float circularRadius;
    /*** 发射器的中心x坐标 */
    private float emitterCenterX;
    /*** 发射器的中心y坐标 */
    private double emitterCenterY;
    /*** 发射器半径 */
    private float emitterRadius;

    public BubbleControllerBuilder setMinBubbleRadius(float minBubbleRadius) {
      this.minBubbleRadius = minBubbleRadius;
      return this;
    }

    public BubbleControllerBuilder setMaxBubbleRadius(float maxBubbleRadius) {
      this.maxBubbleRadius = maxBubbleRadius;
      return this;
    }

    public BubbleControllerBuilder setSpeedRadio(float speedRadio) {
      this.speedRadio = speedRadio;
      return this;
    }

    public BubbleControllerBuilder setRadiusRadio(float radiusRadio) {
      this.radiusRadio = radiusRadio;
      return this;
    }

    public BubbleControllerBuilder setMinBubbleSpeedY(float minBubbleSpeedY) {
      this.minBubbleSpeedY = minBubbleSpeedY;
      return this;
    }

    public BubbleControllerBuilder setMaxBubbleSpeedY(float maxBubbleSpeedY) {
      this.maxBubbleSpeedY = maxBubbleSpeedY;
      return this;
    }

    public BubbleControllerBuilder setMaxBubbleCount(int maxBubbleCount) {
      this.maxBubbleCount = maxBubbleCount;
      return this;
    }

    public BubbleControllerBuilder setRd(Random rd) {
      this.rd = rd;
      return this;
    }

    public BubbleControllerBuilder setCircularCenterX(int circularCenterX) {
      this.circularCenterX = circularCenterX;
      return this;
    }

    public BubbleControllerBuilder setCircularCenterY(int circularCenterY) {
      this.circularCenterY = circularCenterY;
      return this;
    }

    public BubbleControllerBuilder setCircularRadius(float circularRadius) {
      this.circularRadius = circularRadius;
      return this;
    }

    public BubbleControllerBuilder setEmitterCenterX(float emitterCenterX) {
      this.emitterCenterX = emitterCenterX;
      return this;
    }

    public BubbleControllerBuilder setEmitterCenterY(double emitterCenterY) {
      this.emitterCenterY = emitterCenterY;
      return this;
    }

    public BubbleControllerBuilder setEmitterRadius(float emitterRadius) {
      this.emitterRadius = emitterRadius;
      return this;
    }

    @Override
    public BubbleController build() {
      return new BubbleController(this);
    }
  }

  /**
   * 生成气泡
   */
  public void generateBubble() {

    if (mBubbleList.size() >= maxBubbleCount) return;
    //延迟生成气泡的作用
    if (rd.nextBoolean() || rd.nextBoolean() || rd.nextBoolean()) return;

    int bubbleAlpha = 255 / 2 + rd.nextInt(255 / 2);
    float radius = minBubbleRadius + rd.nextInt((int) (maxBubbleRadius - minBubbleRadius));

    //气泡的x起始坐标
    float x = circularCenterX - emitterRadius + radius + rd.nextInt((int) (2 * (emitterRadius - radius)));
    //气泡的y起始坐标
    float y = circularCenterY * 2 + radius;
    //气泡的起始速度
    float speedY = minBubbleSpeedY + rd.nextFloat() * (maxBubbleSpeedY - minBubbleSpeedY);
    float sizeRadio = radiusRadio * rd.nextFloat();

    ChargingHelper.generateBubble(mBubbleList, x, y, radius, speedY, bubbleAlpha, color, sizeRadio);
  }

  /**
   * 遍历气泡
   */
  public void performTraversals() {

    for (int i = 0; i < mBubbleList.size(); i++) {
      Bubble b = mBubbleList.get(i);
      if (b.getRadius() < minBubbleRadius || hitCircular(b)) {
        mBubbleList.remove(b);
        if (mBubblePathMap.containsKey(b)) {
          mBubblePathMap.remove(b);
        }
        i--;
        continue;
      }

      b.setY(b.getY() - b.getSpeedY());

      if (!b.isUnAccessible()) {
        //改变半径
        b.setRadius(b.getRadius() - b.getSizeRadio());
      }
      changeBubblePath(b);
    }

  }


  /**
   * 改变气泡的路径
   *
   * @param b
   */
  private void changeBubblePath(Bubble b) {

    Path path = mBubblePathMap.get(b);
    if (path == null) {
      path = new Path();
      mBubblePathMap.put(b, path);
    } else {
      path.reset();
    }
    //气泡圆心和发射器圆心距离
    double beDistance = getCenterDistance(b.getX(), b.getY(), emitterCenterX, (float) emitterCenterY);
    //气泡圆心和圆环中心距离
    double bcDistance = getCenterDistance(b.getX(), b.getY(), circularCenterX, circularCenterY);

    generatePath(b, path, beDistance, bcDistance);
  }

  private void generatePath(Bubble b, Path path, double beDistance, double bcDistance) {

    path.addCircle(b.getX(), b.getY(), b.getRadius(), Path.Direction.CCW);

    //气泡的界限
    if (bcDistance >= circularRadius + b.getRadius() && bcDistance < circularRadius + b.getRadius() + b.getRadius()) { //靠近圆环
      //判断是否有粘性
      if (b.getRadius() >= minBubbleRadius) {
        //圆环-气泡半径方向的偏移
        float offset = b.getRadius();
        //偏移的距离
        float offsetDistance = b.getRadius();

        if (b.getSpeedY() > minBubbleSpeedY) {
          b.setSpeedY(b.getSpeedY() * speedRadio);
        }

        //获取弧度
        double rad = DegreeUtil.getCoordinateRadians(circularCenterX - b.getX(), circularCenterY - b.getY());
        //得到角度
        double degree = DegreeUtil.toDegrees(rad);
        //y方向上的偏移量
        double yOffset = DegreeUtil.getSinSideLength(offset, rad);
        //这段偏移会耗费的时长
        double t = yOffset / b.getSpeedY();
        //运行多久了
        double d = circularRadius + b.getRadius() + offset - bcDistance;
        //y方向上运行了多长距离
        double yd = DegreeUtil.getSinSideLength(d, rad);
        //已经消耗的时间
        double ht = yd * t / yOffset;

        bubblePoints[3].x = (float) (circularCenterX + DegreeUtil.getCosSideLength(circularRadius, rad));
        bubblePoints[3].y = (float) (circularCenterY + DegreeUtil.getSinSideLength(circularRadius, rad));

        //左右两侧的点
        float sinSideLen = (float) (b.getRadius() + ht * offsetDistance / t);
        double cosSideLen = Math.sqrt(Math.pow(circularRadius, 2) - Math.pow(sinSideLen, 2));
        PointF[] p2 = ChargingHelper.generatePoints(circularCenterX, circularCenterY, degree, cosSideLen, sinSideLen, true);
        bubblePoints[2] = p2[0];
        bubblePoints[4] = p2[1];

        //对边长度
        float sinSideLength2 = (float) (b.getRadius() / 2 + ht * b.getRadius() / (2 * t));
        //邻边长度
        double cosSideLength2 = Math.sqrt(Math.pow(b.getRadius(), 2) - Math.pow(sinSideLength2, 2));
        //直角边长度
        double cosSideLength3 = bcDistance - cosSideLength2;
        PointF[] p3 = ChargingHelper.generatePoints(circularCenterX, circularCenterY, degree, cosSideLength3, sinSideLength2, true);
        bubblePoints[0] = p3[0];
        bubblePoints[6] = p3[1];

        //邻边长度
        double cosSideLength4 = circularRadius + (bcDistance - circularRadius - b.getRadius()) * C;
        //对角边长度
        double sinSideLength4 = sinSideLen * C;
        PointF[] p4 = ChargingHelper.generatePoints(circularCenterX, circularCenterY, degree, cosSideLength4, sinSideLength4, true);
        bubblePoints[1] = p4[0];
        bubblePoints[5] = p4[1];

        ChargingHelper.connectToPath(path, bubblePoints, lineSmoothness);
        //半径不再变
        b.setUnAccessible(true);
      }
    } else if (beDistance >= emitterRadius + b.getRadius() && beDistance < emitterRadius + b.getRadius() + b.getRadius() / 2) { //靠近发射器(离开时)

      //发射器-气泡半径方向的偏移
      float offset = b.getRadius() / 2;

      float offsetDistance = b.getRadius() / 3;

      if (b.getSpeedY() > (minBubbleSpeedY + maxBubbleSpeedY) / 2) {
        b.setSpeedY(b.getSpeedY() * (1 - speedRadio * speedRadio));
      }

      //获取弧度
      double rad = DegreeUtil.getCoordinateRadians2(emitterCenterX - b.getX(), emitterCenterY - b.getY());
      //得到角度
      double degree = DegreeUtil.toDegrees(rad);
      //y方向上的偏移量
      double yOffset = DegreeUtil.getSinSideLength(offset, rad);
      //这段偏移会耗费的时长
      double t = yOffset / b.getSpeedY();
      //运行多久了
      double d = beDistance - emitterRadius - b.getRadius();
      //y方向上运行了多长距离
      double yd = DegreeUtil.getSinSideLength(d, rad);
      //已经消耗的时间
      double ht = yd * t / yOffset;

      bubblePoints[3].x = (float) (emitterCenterX + DegreeUtil.getCosSideLength(emitterRadius, rad));
      bubblePoints[3].y = (float) (emitterCenterY - DegreeUtil.getSinSideLength(emitterRadius, rad));

      //左右两侧的点
      double sinSideLen = b.getRadius() - ht * offsetDistance / t;
      double cosSideLen = Math.sqrt(Math.pow(emitterRadius, 2) - Math.pow(sinSideLen, 2));
      PointF[] p2 = ChargingHelper.generatePoints(emitterCenterX, (float) emitterCenterY, degree, cosSideLen, sinSideLen, false);
      bubblePoints[4] = p2[0];
      bubblePoints[2] = p2[1];

      //对边长度
      float sinSideLength2 = (float) (b.getRadius() - ht * b.getRadius() / (2 * t));
      //邻边长度
      double cosSideLength2 = Math.sqrt(Math.pow(b.getRadius(), 2) - Math.pow(sinSideLength2, 2));
      //直角边长度
      double cosSideLength3 = beDistance - cosSideLength2;
      PointF[] p3 = ChargingHelper.generatePoints(emitterCenterX, (float) emitterCenterY, degree, cosSideLength3, sinSideLength2, false);
      bubblePoints[6] = p3[0];
      bubblePoints[0] = p3[1];

      //邻边长度
      double cosSideLength4 = emitterRadius + (beDistance - emitterRadius - b.getRadius()) * C;
      //对角边长度
      double sinSideLength4 = sinSideLen * C;
      PointF[] p4 = ChargingHelper.generatePoints(emitterCenterX, (float) emitterCenterY, degree, cosSideLength4, sinSideLength4, false);
      bubblePoints[5] = p4[0];
      bubblePoints[1] = p4[1];

      ChargingHelper.connectToPath(path, bubblePoints, lineSmoothness);

    } else {
      b.setSpeedY(b.getOriginalSpeedY());
    }
  }


  /**
   * 获取气泡中心和发射器中心的距离平方
   *
   * @return
   */
  private double getCenterDistance(float x1, float y1, float x2, float y2) {
    PointF p1 = new PointF(x1, y1);
    PointF p2 = new PointF(x2, y2);
    return GeometryUtil.getDistanceBetween2Points(p1, p2);
  }

  /**
   * 修改颜色
   *
   * @param color
   */
  public void setColor(int color) {
    this.color = color;
    mBubblePathMap.clear();
    mBubbleList.clear();
  }

  /**
   * 绘制气泡
   *
   * @param canvas
   */
  public void drawBubble(Canvas canvas, Paint paint) {
    for (int i = 0; i < mBubbleList.size(); i++) {
      Bubble b = mBubbleList.get(i);
      paint.setColor(b.getColor());
      Path path = mBubblePathMap.get(b);
      if (path != null) {
        canvas.drawPath(path, paint);
      }
    }
  }

  /**
   * 是否碰撞到量圆环,如果碰撞到了,则气泡消失
   *
   * @param b
   * @return
   */
  private boolean hitCircular(Bubble b) {
    return getCenterDistance(circularCenterX,
        circularCenterY, b.getX(), b.getY()) <= circularRadius + b.getRadius();

  }

}
