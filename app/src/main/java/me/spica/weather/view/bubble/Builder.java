package me.spica.weather.view.bubble;

/**
 * @ClassName Builder
 * @Author Spica2 7
 * @Date 2023/2/7 9:41
 */
public abstract class Builder<T extends Builder> {
  /**
   * 颜色
   */
  public int color;

  /*** path系数 */
  public float lineSmoothness;

  public T setColor(int color) {
    this.color = color;
    return (T)this;
  }

  public T setLineSmoothness(float lineSmoothness) {
    this.lineSmoothness = lineSmoothness;
    return (T)this;
  }

  public abstract Object build();
}