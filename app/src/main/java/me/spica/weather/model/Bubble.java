package me.spica.weather.model;

/**
 * @ClassName Bubble
 * @Description 气泡单元
 * @Author Spica2 7
 * @Date 2023/2/7 9:38
 */
public class Bubble {


  /**
   * 颜色
   */
  private int color;

  /**
   * 透明度
   */
  private float alpha;

  /**
   * y方向的速度
   */
  private float speedY;

  /**
   * 气泡的x,y坐标
   */
  private float x, y;

  /**
   * 尺寸变化率
   */
  private float sizeRadio;

  /**
   * 气泡半径
   */
  private float radius;

  /**
   * 半径是否可变,false表示可变,true表示不可变
   */
  private boolean unAccessible;

  /**
   * 初始速度
   */
  private float originalSpeedY;

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  public float getSpeedY() {
    return speedY;
  }

  public void setSpeedY(float speedY) {
    this.speedY = speedY;
  }

  public float getOriginalSpeedY() {
    return originalSpeedY;
  }

  public Bubble setOriginalSpeedY(float originalSpeedY) {
    this.originalSpeedY = originalSpeedY;
    return this;
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getSizeRadio() {
    return sizeRadio;
  }

  public void setSizeRadio(float sizeRadio) {
    this.sizeRadio = sizeRadio;
  }

  public float getRadius() {
    return radius;
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public boolean isUnAccessible() {
    return unAccessible;
  }

  public void setUnAccessible(boolean unAccessible) {
    this.unAccessible = unAccessible;
  }
}