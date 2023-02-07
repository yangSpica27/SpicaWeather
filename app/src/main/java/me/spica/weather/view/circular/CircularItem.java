package me.spica.weather.view.circular;

import android.graphics.Path;
import android.graphics.PointF;

/**
 * @ClassName CircularItem
 * @Author Spica2 7
 * @Date 2023/2/7 13:36
 */
public class CircularItem {
  private String TAG = "CircularSeg";

  /*** 偏移量 */
  private float offset;

  /*** 偏移增大率 */
  private float offsetRadio;

  /*** 表示偏移量变小 */
  private boolean isDecreaseOffset;

  /*** path系数 */
  private float lineSmoothness;

  /*** 不变的路径 */
  private Path mStickPath;

  /*** 变化的路径 */
  private Path mDynamicPath;

  /*** 圆环中心坐标 */
  private int circularCenterX, circularCenterY;

  /*** 圆环半径 */
  private float circularRadius;

  /*** 中间的弧度值 */
  private double midRad;

  /*** 旋转速度 */
  private float rotateAngle;

  /*** 正常的旋转速率 */
  private float rotateRadio;

  /*** 快速的旋转速率 */
  private float quickRotateRadio;

  /*** 较慢的旋转速率 */
  private float slowRotateRadio;

  /*** 总的旋转角度差 */
  private float dValueAngle;

  /*** 总的旋转角度差 最大值 */
  private float maxDValueAngle;

  /*** 是否是快速旋转 */
  private boolean isQuickRotate;

  /**
   * 圆环 某段圆弧上的3个点, 起始点,中间点,结束点
   * mStickPoints[0], mStickPoints[2]为圆弧上交点,mStickPoints[1]为这段圆弧的中心点
   */
  private PointF[] mStickPoints = new PointF[3];
  /**
   * 圆环凸出部分 某段圆弧上的3个点, 起始点,中间点,结束点
   * mDynamicPoints[2], mDynamicPoints[0]为圆弧上交点,mDynamicPoints[1]为这段圆弧的中心点
   */
  private PointF[] mDynamicPoints = new PointF[3];

  /*** 透明度 */
  private int alpha;

  /*** 透明度增加或减小的速率 */
  private int alphaRadio;

  /*** 最大透明度 */
  private int maxAlpha;

  /*** 最小透明度 */
  private int minAlpha;

  /*** 透明度是否减小*/
  private boolean isDecreaseAlpha;

  /*** 画笔颜色 */
  private int color;

  /*** 快速旋转的起始角度*/
  private int quickRotateStartAngle;

  /*** 设置最大偏移量 */
  private float offsetMax;
}
