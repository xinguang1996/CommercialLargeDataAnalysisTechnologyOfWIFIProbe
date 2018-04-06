
# -*- coding: utf-8 -*-
# filename: menu.py
import urllib
from basic import Basic

class Menu(object):
    def __init__(self):
        pass
    def create(self, postData, accessToken):
        postUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s" % accessToken
        if isinstance(postData, unicode):
            postData = postData.encode('utf-8')
        urlResp = urllib.urlopen(url=postUrl, data=postData)
        print urlResp.read()

    def query(self, accessToken):
        postUrl = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=%s" % accessToken
        urlResp = urllib.urlopen(url=postUrl)
        print urlResp.read()

    def delete(self, accessToken):
        postUrl = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s" % accessToken
        urlResp = urllib.urlopen(url=postUrl)
        print urlResp.read()
        
    #获取自定义菜单配置接口
    def get_current_selfmenu_info(self, accessToken):
        postUrl = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=%s" % accessToken
        urlResp = urllib.urlopen(url=postUrl)
        print urlResp.read()

if __name__ == '__main__':
    myMenu = Menu()
    postJson = """
{
    "button": [
        {
            "name": "设备控制",
            "sub_button": [
                {
                    "type": "click",
                    "name": "设备开机",
                    "key": "boot"
                },
                {
                    "type": "click",
                    "name": "设备关机",
                    "key": "shutdown"
                },
                {
                    "type": "click",
                    "name": "设备重启",
                    "key": "reboot"
                }
            ]
        },
        {
            "name": "客流查询",
            "sub_button": [
                {
                    "type": "click",
                    "name": "最近一时",
                    "key": "hour"
                },
                {
                    "type": "click",
                    "name": "最近一天",
                    "key": "day"
                },
                {
                    "type": "click",
                    "name": "最近一周",
                    "key": "week"
                },
                {
                    "type": "click",
                    "name": "最近一月",
                    "key": "month"
                }
            ]
        }
    ]
}
    """
    accessToken = Basic().get_access_token()
    #myMenu.delete(accessToken)
    myMenu.create(postJson, accessToken)

