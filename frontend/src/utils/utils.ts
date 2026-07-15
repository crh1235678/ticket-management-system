//工具方法

//引进utils中常量
import Constants from '@/utils/constants';

//localStorage 是webapi自带的方法
//往浏览器本地存数据，存入key和value
export const saveLocalStorage = (key: string, value: string) => {
  localStorage.setItem(key, value);
};

//从浏览器本地取数据，输入key，返回value
export const getLocalStorage = (key: string) => {
  return localStorage.getItem(key) || '';
};

//清除浏览器本地数据
export const clearLocalStorage = () => {
  localStorage.clear();
};

//把后端返回的“相对路径” → 变成“完整访问地址”
export const getFileUrl = (url: string) => {
  return Constants.BASE_URL + '/' + url;
};

//把后端返回的“多个文件相对路径” → 变成“多个文件完整访问地址”
export const urls2FileUrl = (url: any) => {
    //定义临时数组
    const tlist: any = [];
    if (url){
        //把字符串通过“逗号”分隔成数组，map遍历数组，每个元素都调用getFileUrl方法，把相对路径变成完整访问地址
        url.split(',').map((item: string) => {
            let url = getFileUrl(item);
            const file = {
                name: item,
                url: url
            };
            //把完整访问地址保存到tlist中
            tlist.push(file);
            });
    }
    return tlist;
};
