layui.use(["layer", "ztree", "form", "ax"], function() {
  var $ = layui.$;
  var $ax = layui.ax;
  var layer = layui.layer;
  var form = layui.form;
  var $ZTree = layui.ztree;

  form.on("select(tableName)", function(data) {
    var preSize = $("#ignoreTabelPrefix").val().length;
    $("#tableName").val(data.value);
    $("#className").val(Feng.underLineToCamel(data.value.substring(preSize)));
  });

  //监听提交
  form.on("submit(formDemo)", function(data) {
    if (!data.field.tableName) {
      Feng.info("请选择要生成的表");
      return false;
    }
    if ($("#codeForm input[name=templateName]:checked").length == 0) {
      Feng.info("请选择模板");
      return false;
    }

    $("#codeForm input[name=templateName]:checked").each(function() {
      data.field[$(this).val()] = true;
    });

    $.post("/manage/code/generate", data.field, function() {
      layer.msg("代码自动创建完成！");
    });
    return false;
  });

  /**
   * 初始化
   */
  var Code = {
    ztreeInstance: null
  };

  /**
   * 点击父级编号input框时
   */
  Code.onClickDept = function(e, treeId, treeNode) {
    $("#parentMenuName").attr("value", Code.ztreeInstance.getSelectedVal());
  };

  var ztree = new $ZTree("pcodeTree", "/manage/menu/selectMenuTreeList");
  ztree.bindOnClick(Code.onClickDept);
  ztree.init();
  Code.ztreeInstance = ztree;
  $("#pcodeTree").css("width", $("#parentMenuName").css("width"));
  $("#parentMenuName").click(function() {
    Feng.showInputTree("parentMenuName", "pcodeTreeDiv", 0, 38);
  });
});
