package com.eason.wmpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eason.wmpt.entity.AddressBook;
import com.eason.wmpt.mapper.AddressBookMapper;
import com.eason.wmpt.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
