package org.example.filetool.TooUtil;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

public class TargetDataListener extends AnalysisEventListener<TargetData> {
    private final List<TargetData> dataList = new ArrayList<>();

    @Override
    public void invoke(TargetData data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 读取完成后的操作
    }

    public List<TargetData> getDataList() {
        return dataList;
    }
}