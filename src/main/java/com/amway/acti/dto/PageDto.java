/**
 * Created by Will Zhang on 2018/2/23.
 */

package com.amway.acti.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageDto<T>
{

  /**
   * 数据集合
   */
  private List<T> dataList;

  /**
   * 当前页
   */
  private int currentPage;

  /**
   * 总页数
   */
  private int totalPages;

  private long totalCount;
}
