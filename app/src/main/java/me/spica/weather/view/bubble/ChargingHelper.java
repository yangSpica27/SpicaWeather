package me.spica.weather.view.bubble;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import java.util.List;
import me.spica.weather.model.Bubble;
import me.spica.weather.tools.DegreeUtil;

/**
 * @ClassName ChargingHelper
 * @Description TODO
 * @Author Spica2 7
 * @Date 2023/2/7 9:43
 */
public class ChargingHelper {


  /**
   * 生成气泡对象,并添加到气泡集合中
   *
   * @param bubbleList
   * @param x
   * @param y
   * @param radius
   * @param speedY
   * @param alpha
   * @param color
   * @param sizeRadio
   */

  public static void generateBubble(List<Bubble> bubbleList, float x, float y, float radius,
                                    float speedY, int alpha, int color, float sizeRadio) {

    if (bubbleList == null) throw new NullPointerException("bubbleList == null");
    Bubble b = new Bubble();
    b.setX(x);
    b.setY(y);
    b.setRadius(radius);
    b.setOriginalSpeedY(speedY);
    b.setSpeedY(speedY);
    b.setAlpha(alpha);
    b.setColor(getComposeColor(alpha, color));
    b.setSizeRadio(sizeRadio);
    bubbleList.add(b);
  }

  /**
   * 获取组合后的颜色
   *
   * @param alpha
   * @param color
   * @return
   */
  public static int getComposeColor(int alpha, int color) {
    int red = Color.red(color);
    int green = Color.green(color);
    int blue = Color.blue(color);
    int composeColor = alpha << 24 | red << 16 | green << 8 | blue;
    return composeColor;
  }


  /**
   * 生成2个控制点
   *
   * @param p0
   * @param p1
   * @param p2
   * @param lineSmoothness
   * @return
   */
  public static PointF[] generateMidControlPoints(PointF p0, PointF p1, PointF p2, float lineSmoothness) {

    float c1x = p0.x + (p1.x - p0.x) * lineSmoothness;
    float c1y = p0.y + (p1.y - p0.y) * lineSmoothness;

    float c2x = p1.x - (p2.x - p0.x) * lineSmoothness;
    float c2y = p1.y - (p2.y - p0.y) * lineSmoothness;

    PointF[] points = new PointF[2];
    points[0] = new PointF(c1x, c1y);
    points[1] = new PointF(c2x, c2y);
    return points;
  }

  /**
   * 生成最后的2个控制点
   *
   * @param p0
   * @param p1
   * @param p2
   * @param lineSmoothness
   * @return
   */
  public static PointF[] generateLastControlPoints(PointF p0, PointF p1, PointF p2, float lineSmoothness) {

    float c1x = p1.x + (p2.x - p0.x) * lineSmoothness;
    float c1y = p1.y + (p2.y - p0.y) * lineSmoothness;

    float c2x = p2.x - (p2.x - p1.x) * lineSmoothness;
    float c2y = p2.y - (p2.y - p1.y) * lineSmoothness;

    PointF[] points = new PointF[2];
    points[0] = new PointF(c1x, c1y);
    points[1] = new PointF(c2x, c2y);
    return points;
  }

  /**
   * 生成圆弧上的3个点
   *
   * @param centerX    中心点x
   * @param centerY    中心点y
   * @param radius     半径
   * @param startAngle 起始角度
   * @param sweepAngle 扫描角度
   * @return
   */
  public static PointF[] generatePoints(float centerX, float centerY, float radius, float startAngle, float sweepAngle) {

    double startRad = DegreeUtil.toRadians(startAngle);
    double midRad = DegreeUtil.toRadians(startAngle + sweepAngle / 2);
    double endRad = DegreeUtil.toRadians(startAngle + sweepAngle);
    //起始点
    double startX = centerX + DegreeUtil.getCosSideLength(radius, startRad);
    double startY = centerY + DegreeUtil.getSinSideLength(radius, startRad);
    //圆弧中点
    double midX = centerX + DegreeUtil.getCosSideLength(radius, midRad);
    double midY = centerY + DegreeUtil.getSinSideLength(radius, midRad);
    //结束点
    double endX = centerX + DegreeUtil.getCosSideLength(radius, endRad);
    double endY = centerY + DegreeUtil.getSinSideLength(radius, endRad);

    PointF[] points = new PointF[3];

    points[0] = new PointF((float) startX, (float) startY);
    points[1] = new PointF((float) midX, (float) midY);
    points[2] = new PointF((float) endX, (float) endY);
    return points;
  }

  /**
   * 连成path
   *
   * @param path
   * @param points
   * @param lineSmoothness
   */
  public static void connectToPath(Path path, PointF[] points, float lineSmoothness) {

    if (points == null) throw new NullPointerException("points == null");
    if (points.length < 3) throw new IllegalArgumentException("points.length < 3");
    PointF[] c = null;
    for (int i = 0; i < points.length; i++) {

      if (i == 0) {
        path.moveTo(points[i].x, points[i].y);
      } else if (i == points.length - 1) {
        c = ChargingHelper.generateLastControlPoints(points[i - 2], points[i - 1], points[i], lineSmoothness);
        path.cubicTo(c[0].x, c[0].y, c[1].x, c[1].y, points[i].x, points[i].y);
      } else {
        c = ChargingHelper.generateMidControlPoints(points[i - 1], points[i], points[i + 1], lineSmoothness);
        path.cubicTo(c[0].x, c[0].y, c[1].x, c[1].y, points[i].x, points[i].y);
      }
    }
  }

  /**
   * 生成2个点
   * @param centerX
   * @param centerY
   * @param angle
   * @param cosSideLength
   * @param sinSideLength
   * @param isYAdd
   * @return
   */
  public static PointF[] generatePoints(float centerX, float centerY, double angle, double cosSideLength, double sinSideLength, boolean isYAdd){
    double rad = DegreeUtil.getCoordinateRadians(cosSideLength, sinSideLength);
    double degree = DegreeUtil.toDegrees(rad);
    //斜边长度
    double hypotenuse = Math.sqrt(Math.pow(cosSideLength, 2) + Math.pow(sinSideLength, 2));

    double rad1 = DegreeUtil.toRadians(angle - degree);
    //x1,y1坐标
    double x1 = centerX + DegreeUtil.getCosSideLength(hypotenuse, rad1);

    double rad2 = DegreeUtil.toRadians(angle + degree);
    //x2,y2坐标
    double x2 = centerX + DegreeUtil.getCosSideLength(hypotenuse, rad2);

    double y1 = 0, y2 = 0;
    if(isYAdd){
      y1 = centerY + DegreeUtil.getSinSideLength(hypotenuse, rad1);
      y2 = centerY + DegreeUtil.getSinSideLength(hypotenuse, rad2);
    }else{
      y1 = centerY - DegreeUtil.getSinSideLength(hypotenuse, rad1);
      y2 = centerY - DegreeUtil.getSinSideLength(hypotenuse, rad2);
    }
    PointF []points = new PointF[2];
    points[0] = new PointF((float) x1,(float) y1);
    points[1] = new PointF((float) x2,(float) y2);
    return points;
  }


}
