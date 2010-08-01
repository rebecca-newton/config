/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.xml.fieldset;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.xml.model.ValueXmlItem;
import org.jboss.seam.xml.util.TypeReader;
import org.jboss.weld.extensions.util.properties.Property;

/**
 * class responsible for setting the value of collection properties.
 * 
 * It can deal with the following collection types: -Set -List -Collection
 * -SortedSet -HashSet -ArrayList -TreeSet -LinkedList
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
public class CollectionFieldSet implements FieldValueObject
{
   private final Property field;
   private final List<FieldValue> values;
   private final Class<?> elementType;
   private final Class<? extends Collection> collectionType;

   public CollectionFieldSet(Property field, List<ValueXmlItem> items)
   {
      this.field = field;
      this.values = new ArrayList<FieldValue>();

      Type type = field.getBaseType();
      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;

         if (parameterizedType.getRawType() == Collection.class)
         {
            collectionType = LinkedHashSet.class;
         }
         else if (parameterizedType.getRawType() == List.class)
         {
            collectionType = ArrayList.class;
         }
         else if (parameterizedType.getRawType() == Set.class)
         {
            collectionType = LinkedHashSet.class;
         }
         else if (parameterizedType.getRawType() == SortedSet.class)
         {
            collectionType = TreeSet.class;
         }
         else if (parameterizedType.getRawType() == HashSet.class)
         {
            collectionType = HashSet.class;
         }
         else if (parameterizedType.getRawType() == ArrayList.class)
         {
            collectionType = ArrayList.class;
         }
         else if (parameterizedType.getRawType() == LinkedList.class)
         {
            collectionType = LinkedList.class;
         }
         else if (parameterizedType.getRawType() == LinkedHashSet.class)
         {
            collectionType = LinkedHashSet.class;
         }
         else if (parameterizedType.getRawType() == TreeSet.class)
         {
            collectionType = TreeSet.class;
         }
         else
         {
            throw new RuntimeException("Could not determine element type for " + field.getDeclaringClass().getName() + "." + field.getName());
         }
         elementType = TypeReader.readClassFromType(parameterizedType.getActualTypeArguments()[0]);
      }
      else
      {
         throw new RuntimeException("Could not determine element type for " + field.getDeclaringClass().getName() + "." + field.getName());
      }

      for (ValueXmlItem i : items)
      {
         values.add(i.getValue());
      }
   }

   public void setValue(Object instance, CreationalContext<?> ctx, BeanManager manager)
   {
      try
      {
         Collection<Object> res = collectionType.newInstance();
         field.setValue(instance, res);
         for (int i = 0; i < values.size(); ++i)
         {
            res.add(values.get(i).value(elementType, ctx, manager));
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
